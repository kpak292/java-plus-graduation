package ru.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.event.ActionTypeProto;
import ru.practicum.grpc.stats.event.UserActionProto;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface UserActionMapper {
    UserActionMapper INSTANCE = Mappers.getMapper(UserActionMapper.class);

    UserActionAvro toUserActionAvro(UserActionProto userActionProto);

    default Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    default ActionTypeAvro toAvroType(ActionTypeProto type){
        return switch (type){
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            default -> throw new IllegalArgumentException("Unknown action type: " + type);
        };
    }
}
