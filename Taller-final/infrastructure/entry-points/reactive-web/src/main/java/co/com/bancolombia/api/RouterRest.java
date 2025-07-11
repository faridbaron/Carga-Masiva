package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(BoxHandler boxHandler) {
        return route(PUT("/api/boxes/close/{id}"), boxHandler::close)
                .andRoute(PUT("/api/boxes/open/{id}"), boxHandler::open)
                .and(route(POST("/api/boxes"), boxHandler::createBox))
                .and(route(GET("/api/boxes/{id}"), boxHandler::getByIdBox))
                .and(route(GET("/api/boxes"), boxHandler::listAllBoxes))
                .and(route(PUT("/api/boxes/{id}"), boxHandler::updateNameBox))
                .and(route(DELETE("/api/boxes/{id}"), boxHandler::deleteBox))
                .andRoute(PUT("/api/boxes/reopen/{id}"), boxHandler::reopenBox);
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunctionMovement(MovementHandler movementHandler) {
        return route(POST("/api/boxes/{boxId}/movements/upload"), movementHandler::uploadMovements);
    }
    @Bean
    public RouterFunction<ServerResponse> routerFunctionAudit(AuditHandler auditHandler) {
        return route(GET("/api/upload/audit/history"), auditHandler::getAllHistoryAudit);
    }
}
