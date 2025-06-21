package ru.practicum.client;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.event.*;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnalyzerClient {
    @GrpcClient("analyzer")
    RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        return Lists.newArrayList(client.getInteractionsCount(request));
    }

    public List<RecommendedEventProto> getSimilarEvent(SimilarEventsRequestProto request) {
        return Lists.newArrayList(client.getSimilarEvents(request));
    }

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        return Lists.newArrayList(client.getRecommendationsForUser(request));
    }
}
