package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.AggregatorMapper;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.service.AggregatorService;
import ru.practicum.service.SimilarityService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregatorService {
    private final UserActionRepository userActionRepository;
    private final SimilarityService similarityService;

    @Override
    public void saveUpdate(UserActionAvro userActionAvro) {
        UserAction userAction = AggregatorMapper.INSTANCE.toUserAction(userActionAvro);
        userAction.setScore(similarityService.getRate(userActionAvro.getActionType()));

        Optional<UserAction> savedUserAction = userActionRepository.findByUserIdAndEventId(userAction.getUserId(), userAction.getEventId());

        if (savedUserAction.isEmpty()) {
            userActionRepository.save(userAction);
            similarityService.sendUpdate(userAction.getUserId(), userAction.getEventId(), 0D);
        } else if (savedUserAction.get().getScore() < userAction.getScore()) {
            UserAction updatedUserAction = savedUserAction.get();

            similarityService.sendUpdate(userAction.getUserId(), userAction.getEventId(),
                    userAction.getScore() - savedUserAction.get().getScore());

            updatedUserAction.setScore(userAction.getScore());
            updatedUserAction.setTimestamp(userAction.getTimestamp());
            userActionRepository.save(updatedUserAction);
        }
    }
}
