package io.joshuasalcedo.commandcenter.config.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Forwards non-backend routes to the Next.js static export so
 * client-side routing works.
 * <p>
 * The regex {@code (?!api|swagger-ui|v3|actuator).*} ensures paths
 * starting with api/, swagger-ui/, v3/, or actuator/ are never matched,
 * leaving them to their respective REST controllers.
 */
@Controller
public class SpaForwardingController {

    private static final String INDEX_FORWARD = "forward:/index.html";
    private static final String EXCLUDE = "(?!api|swagger-ui|v3|actuator)[^\\\\.]*";

    @GetMapping("/")
    public String root() {
        return INDEX_FORWARD;
    }

    @GetMapping("/{path:" + EXCLUDE + "}")
    public String forwardSingle(@PathVariable String path) {
        return INDEX_FORWARD;
    }

    @GetMapping("/{path:" + EXCLUDE + "}/{subpath:[^\\\\.]*}")
    public String forwardNested(@PathVariable String path, @PathVariable String subpath) {
        return INDEX_FORWARD;
    }

    @GetMapping("/{path:" + EXCLUDE + "}/{subpath:[^\\\\.]*}/{rest:[^\\\\.]*}")
    public String forwardDeep(@PathVariable String path, @PathVariable String subpath, @PathVariable String rest) {
        return INDEX_FORWARD;
    }
}
