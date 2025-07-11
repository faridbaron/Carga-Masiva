package co.com.bancolombia.events;
import co.com.bancolombia.events.handlers.EventsHandler;
import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerRegistryConfiguration {

    @Bean
    public HandlerRegistry handlerRegistry(EventsHandler events) {
        return HandlerRegistry.register()
                .listenEvent("box.event.created", events::handleEventCreated, Object.class)
                .listenEvent("box.event.name.updated", events::handleEventUpdate, Object.class)
                .listenEvent("box.event.deleted", events::handleEventDeleted, Object.class)
                .listenEvent("box.event.reopened", events::handleEventReopened, Object.class)
                .listenEvent("movement.event.uploaded", events::handleEventUploaded, Object.class);
    }
}
