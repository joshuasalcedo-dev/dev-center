package io.joshuasalcedo.commandcenter.auth;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
class AuthEventsController {

    private final AuthEventService authEventService;

    AuthEventsController(AuthEventService authEventService) {
        this.authEventService = authEventService;
    }

    @GetMapping(value = "/auth/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter streamAuthEvents() {
        return authEventService.subscribe();
    }
}
