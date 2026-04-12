package io.joshuasalcedo.commandcenter.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
class AuthEventService {

    private static final Logger log = LoggerFactory.getLogger(AuthEventService.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    SseEmitter subscribe() {
        var emitter = new SseEmitter(120_000L); // 2 minute timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    void emitCredentials(String credentialsJson) {
        for (var emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("credentials")
                        .data(credentialsJson));
                emitter.complete();
            } catch (IOException e) {
                log.debug("Failed to send SSE event, removing emitter", e);
                emitters.remove(emitter);
            }
        }
    }
}
