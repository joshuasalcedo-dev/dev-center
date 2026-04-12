package io.joshuasalcedo.commandcenter.config.security;

import io.joshuasalcedo.commandcenter.apikey.api.ApiKeyService;
import io.joshuasalcedo.commandcenter.apikey.api.dto.ApiKeyDTO;
import io.joshuasalcedo.commandcenter.user.Role;
import io.joshuasalcedo.commandcenter.user.api.UserService;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-Key";

    private final ApiKeyService apiKeyService;
    private final UserService userService;

    ApiKeyAuthenticationFilter(ApiKeyService apiKeyService, UserService userService) {
        this.apiKeyService = apiKeyService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String key = request.getHeader(HEADER);

        if (key != null && !key.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                ApiKeyDTO dto = apiKeyService.authenticate(key);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                userService.findById(dto.ownerId()).ifPresent(user -> {
                    if (user.role() == Role.ADMIN) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                });

                SecurityContextHolder.getContext().setAuthentication(
                        new ApiKeyAuthenticationToken(dto.ownerId(), dto.id(), authorities)
                );
            } catch (Exception ignored) {
                // invalid key — let the request continue unauthenticated;
                // Spring Security will reject it if the endpoint requires auth
            }
        }

        filterChain.doFilter(request, response);
    }
}
