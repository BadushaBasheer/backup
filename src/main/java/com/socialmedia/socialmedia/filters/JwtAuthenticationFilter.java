package com.socialmedia.socialmedia.filters;

import com.socialmedia.socialmedia.services.jwt.UserService;
import com.socialmedia.socialmedia.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            final String authHeader = request.getHeader(AUTHORIZATION);
            final String jwt;
            final String userEmail;

            // Check if the Authorization header is present and starts with "Bearer "
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            // Extract JWT token from the Authorization header
            jwt = authHeader.substring(7);
            // Extract username (email) from the JWT token
            userEmail = jwtUtil.extractUsername(jwt);

            // Check if the username is present and no authentication is currently set in the context
            if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.userDetailService().loadUserByUsername(userEmail);

                // Validate the token and set the authentication context if valid
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Handle exceptions using the configured exception resolver
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
