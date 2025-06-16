package ru.practicum.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.clients.EventClient;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.ParticipationRequestDto;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.dto.event.enums.RequestStatus;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventClient eventClient;


    @Transactional
    @Override
    public ParticipationRequestDto newRequest(long userId, long eventId) {
        if (requestRepository.findByUserIdAndEventId(userId, eventId).isEmpty()) {
            EventDto event = eventClient.findEventById(eventId)
                    .orElseThrow(() -> new ConflictException("Event with id = " + eventId + " not found"));
            if (event.getInitiator() == userId)
                throw new ConflictException("Event initiator can't make a request");
            if (event.getState() != EventState.PUBLISHED)
                throw new ConflictException("Event with id = " + eventId + " is not published yet");
            if ((event.getParticipantLimit() != 0) && (event.getParticipantLimit() <= event.getConfirmedRequests()))
                throw new ConflictException("Limit of requests reached on event with id = " + event);
            Request request = new Request();
            request.setUserId(userId);
            request.setEventId(eventId);
            request.setCreatedOn(LocalDateTime.now());
            if (event.getParticipantLimit() != 0 && event.getRequestModeration())
                request.setStatus(RequestStatus.PENDING);
            else {
                request.setStatus(RequestStatus.CONFIRMED);
                eventClient.setConfirmed(eventId, event.getConfirmedRequests() + 1L);
            }
            return RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
        } else
            throw new ConflictException("Request from user with id = " + userId +
                                        " on event with id = " + eventId + " already exists");
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id = " + requestId + " not found"));
        if (request.getUserId() != userId)
            throw new ConflictException("User with id = " + userId + " is not an initializer of request with id = " + requestId);
        requestRepository.delete(request);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.INSTANCE.toParticipationRequestDto(request);
    }

    @Override
    public Collection<ParticipationRequestDto> findAllRequestsByUserId(long userId) {
        return requestRepository.findAllByUserId(userId).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList();
    }

    @Override
    public Collection<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId) {
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList();

    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(long userId,
                                                               long eventId,
                                                               EventRequestStatusUpdateRequest request) {
        EventDto event = eventClient.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));

        if (!event.getInitiator().equals(userId))
            throw new ValidationException("User with id = " + userId + " is not a initiator of event with id = " + eventId);

        Collection<Request> requests = requestRepository.findAllRequestsOnEventByIds(eventId,
                request.getRequestIds());
        int limit = event.getParticipantLimit() - event.getConfirmedRequests().intValue();
        int confirmed = event.getConfirmedRequests().intValue();
        if (limit == 0)
            throw new ConflictException("Limit of participant reached");
        for (Request req : requests) {
            if (!req.getStatus().equals(RequestStatus.PENDING))
                throw new ConflictException("Status of the request with id = " + req.getId() + " is " + req.getStatus());
            if (request.getStatus().equals(RequestStatus.REJECTED)) {
                req.setStatus(RequestStatus.REJECTED);
            } else if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                req.setStatus(RequestStatus.CONFIRMED);
                confirmed++;
            } else if (limit == 0) {
                req.setStatus(RequestStatus.REJECTED);
            } else {
                req.setStatus(RequestStatus.CONFIRMED);
                limit--;
            }
            requestRepository.save(req);
        }
        if (event.getParticipantLimit() != 0)
            eventClient.setConfirmed(eventId, (long) event.getParticipantLimit() - limit);
        else
            eventClient.setConfirmed(eventId, confirmed);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(requestRepository.findAllRequestsOnEventByIdsAndStatus(eventId,
                        RequestStatus.CONFIRMED,
                        request.getRequestIds()).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList());
        result.setRejectedRequests(requestRepository.findAllRequestsOnEventByIdsAndStatus(eventId,
                        RequestStatus.REJECTED,
                        request.getRequestIds()).stream()
                .map(RequestMapper.INSTANCE::toParticipationRequestDto)
                .toList());
        return result;
    }
}
