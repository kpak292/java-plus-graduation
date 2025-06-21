package ru.practicum.service.impl;

import com.google.protobuf.Timestamp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.Constants;
import ru.practicum.client.AnalyzerClient;
import ru.practicum.client.CollectorClient;
import ru.practicum.clients.CommentClient;
import ru.practicum.clients.RequestClient;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.event.enums.EventActionStateAdmin;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.dto.event.enums.SortingOptions;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.grpc.stats.event.*;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.EventUpdater;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.service.EventService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final AnalyzerClient analyzerClient;
    private final CollectorClient collectorClient;
    private final CommentClient commentClient;
    private final RequestClient requestClient;

    @Override
    public EventDto save(long userId, NewEventDto newEventDto) {
        LocalDateTime validDate = LocalDateTime.now().plusHours(2L);
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(validDate)) {
            throw new ValidationException("Event date should be after two hours after now");
        }

        Event event = EventMapper.INSTANCE.getEvent(newEventDto);

        Location location = locationRepository.save(event.getLocation());

        event.setInitiator(userId);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);

        return EventMapper.INSTANCE.getEventDto(eventRepository.save(event));
    }

    @Override
    public EventDto findEvent(long eventId, long userId) {
        return findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));
    }

    @Override
    public Optional<EventDto> findEventById(long eventId) {
        return eventRepository.findById(eventId).map(EventMapper.INSTANCE::getEventDto);
    }

    @Override
    public List<EventShortDto> findEvents(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);

        return eventRepository.findByUserId(userId, pageable).stream()
                .map(EventMapper.INSTANCE::getEventShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventDto updateEvent(long eventId, long userId, UpdateEventUserRequest updateEventUserRequest) {
        LocalDateTime validDate = LocalDateTime.now().plusHours(2L);
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(validDate)) {
            throw new ValidationException("Event date should be after two hours after now");
        }

        Event baseEvent = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));

        if (baseEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot updated published event");
        }

        EventUpdater.INSTANCE.update(baseEvent, updateEventUserRequest);

        return EventMapper.INSTANCE.getEventDto(baseEvent);
    }

    @Override
    @Transactional
    public EventDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        LocalDateTime validDate = LocalDateTime.now().plusHours(2L);
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(validDate)) {
            throw new ValidationException("Event date should be after two hours after now");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == EventActionStateAdmin.REJECT_EVENT &&
                event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Cannot cancel published event");
            }

            if (updateEventAdminRequest.getStateAction() == EventActionStateAdmin.PUBLISH_EVENT &&
                event.getState() != EventState.PENDING) {
                throw new ConflictException("Cannot publish event not in status Pending");
            }

            if (updateEventAdminRequest.getStateAction() == EventActionStateAdmin.PUBLISH_EVENT &&
                event.getEventDate().minusHours(1L).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Cannot publish event less than 1 hour before start");
            }
        }

        EventUpdater.INSTANCE.update(event, updateEventAdminRequest);

        if (event.getState() == EventState.PUBLISHED) {
            event.setPublishedOn(LocalDateTime.now());
            event.setConfirmedRequests(0L);
            event.setRating(0D);
        }
        return EventMapper.INSTANCE.getEventDto(event);
    }

    @Override
    public List<EventDto> findEventsByFilter(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, Constants.DATE_TIME_FORMATTER);
        }

        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, Constants.DATE_TIME_FORMATTER);
        }

        return eventRepository.findAllByFilter(users, states, categories, start, end, pageable).stream()
                .map(EventMapper.INSTANCE::getEventDto)
                .toList();
    }

    @Override
    public List<EventShortDto> findEventsByFilterPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, SortingOptions sortingOptions, int from, int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from, size,
                sortingOptions != null
                        ? Sort.by(sortingOptions == SortingOptions.EVENT_DATE ? "eventDate" : "views").descending()
                        : Sort.unsorted());


        LocalDateTime start = rangeStart != null
                ? LocalDateTime.parse(rangeStart, Constants.DATE_TIME_FORMATTER)
                : LocalDateTime.now();

        LocalDateTime end = rangeEnd != null
                ? LocalDateTime.parse(rangeEnd, Constants.DATE_TIME_FORMATTER)
                : null;

        if (end != null && end.isBefore(start)) {
            throw new ValidationException("End is before start");
        }

        List<Event> events = eventRepository.findAllByFilterPublic(text, categories, paid, start, end, onlyAvailable,
                EventState.PUBLISHED, pageable);

        eventRepository.saveAll(events);
        return events.stream()
                .map(EventMapper.INSTANCE::getEventShortDto)
                .toList();
    }

    @Override
    public EventDto findEventPublic(long eventId, long userId) {
        Event baseEvent = eventRepository.findByIdAndStatus(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("published event is not found with id = " + eventId));

        baseEvent.setRating(sendStats(userId, eventId));
        eventRepository.save(baseEvent);
        List<CommentDto> comments = commentClient.getComments(eventId);
        if (!comments.isEmpty()) {
            return EventMapper.INSTANCE.getEventDtoWithComments(baseEvent, comments);
        }
        return EventMapper.INSTANCE.getEventDto(baseEvent);
    }

    @Override
    public void setConfirmed(long eventId, long requests) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));
        if (event.getState() == EventState.PUBLISHED) {
            event.setConfirmedRequests(requests);
        } else {
            throw new ConflictException("Cannot set confirmed requests for event not in status Published");
        }
        eventRepository.save(event);
    }

    @Override
    public List<EventDto> findRecommendations(long userId) {
        List<Long> eventIdList = analyzerClient.getRecommendationsForUser(UserPredictionsRequestProto.newBuilder()
                        .setUserId(userId)
                        .setMaxResults(3)
                        .build()).stream()
                .mapToLong(RecommendedEventProto::getEventId)
                .boxed().toList();

        return eventRepository.findAllById(eventIdList).stream()
                .map(EventMapper.INSTANCE::getEventDto)
                .toList();
    }

    @Override
    public void likeEvent(long userId, long eventId) {
        if (requestClient.checkRegistration(userId, eventId)) {
            collectorClient.sendUserAction(UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(ActionTypeProto.ACTION_LIKE)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(Instant.now().getEpochSecond())
                            .setNanos(Instant.now().getNano())
                            .build())
                    .build());
        }else{
            throw new ConflictException("User is not registered for this event");
        }
    }

    private Double sendStats(Long userId, Long eventId) {
        UserActionProto actionProto = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_VIEW)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();
        collectorClient.sendUserAction(actionProto);

        InteractionsCountRequestProto requestProto = InteractionsCountRequestProto.newBuilder().setEventId(eventId).build();
        return analyzerClient.getInteractionsCount(requestProto).stream().mapToDouble(
                        RecommendedEventProto::getScore)
                .sum();
    }
}
