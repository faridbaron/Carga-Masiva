package co.com.bancolombia.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public org.reactivecommons.utils.ObjectMapper objectMapper() {
        return new org.reactivecommons.utils.ObjectMapperImp();
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        var jacksonObjectMapper = new ObjectMapper();
        jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Add support for Java 8 date/time with JavaTimeModule
        jacksonObjectMapper.registerModule(new JavaTimeModule());

        // To adjust the formatting of date/time objects
        jacksonObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return jacksonObjectMapper;
    }
}
