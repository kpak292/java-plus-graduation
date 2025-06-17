package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rates")
@Getter
@Setter
public class RateConfig {
    private Map<ActionTypeAvro, Double> rates;
}
