package com.ichinae.samples.gateway.handle;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.Optional;

/**
 * @author fuchengwei
 * @date 2020/12/22 6:38 下午
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerUiHandler implements HandlerFunction<ServerResponse> {
    private UiConfiguration uiConfiguration;

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Optional
                        .ofNullable(uiConfiguration)
                        .orElse(UiConfigurationBuilder
                                .builder()
                                .build())));
    }
}
