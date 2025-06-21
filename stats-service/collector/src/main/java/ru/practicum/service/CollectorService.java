package ru.practicum.service;

import ru.practicum.grpc.stats.event.UserActionProto;

public interface CollectorService {
    void createUserAction(UserActionProto request);
}
