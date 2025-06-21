package ru.practicum.client;

import com.google.protobuf.Empty;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.event.UserActionControllerGrpc;
import ru.practicum.grpc.stats.event.UserActionProto;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorClient {
    @GrpcClient("collector")
    UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void sendUserAction(UserActionProto action) {
        Empty empty = client.collectUserAction(action);
    }
}
