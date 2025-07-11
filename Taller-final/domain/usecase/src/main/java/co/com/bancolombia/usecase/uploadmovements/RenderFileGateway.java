package co.com.bancolombia.usecase.uploadmovements;

import reactor.core.publisher.Flux;

import java.util.Map;

public interface RenderFileGateway {
    Flux<Map<String, String>> render(byte[] bytes);
}
