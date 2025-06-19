package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@Slf4j
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    protected final DecoderFactory decoderFactory;
    protected final DatumReader<T> reader;
    protected BinaryDecoder decoder;

    public AvroDeserializer(final Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public AvroDeserializer(final DecoderFactory decoderFactory, final Schema schema) {
        this.decoderFactory = decoderFactory;
        this.reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(final String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        decoder = decoderFactory.binaryDecoder(bytes, decoder);
        try {
            return reader.read(null, decoder);
        } catch (IOException e) {
            log.error("Failed deserialize event", e);
            return null;
        }
    }
}
