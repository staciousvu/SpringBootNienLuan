package com.example.shopapp.filters;

import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.models.User;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Request URI: {}", request.getRequestURI());
        System.out.println(request.getServletPath());
        if (isByPassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED
                    , "authHeader null or not started with Bearer");
            return;
        }
        final String token = authHeader.substring(7);
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = (User) userDetailsService.loadUserByUsername(phoneNumber);
            if (jwtTokenUtils.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user
                                , null
                                , user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isByPassToken(HttpServletRequest request) {
        final List<Pair<String, String>> byPassTokens = Arrays.asList(
                Pair.of(apiPrefix + "/products", "GET"),
                Pair.of(apiPrefix + "/categories", "GET"),
                Pair.of(apiPrefix + "/users/login", "POST"),
                Pair.of(apiPrefix + "/users/register", "POST"),
                Pair.of(apiPrefix + "/roles", "GET"),
                Pair.of( "/uploads", "GET")
        );
        for (Pair<String, String> byPassToken : byPassTokens) {
            if (request.getServletPath().contains(byPassToken.getFirst())) {
                if (request.getMethod().equalsIgnoreCase(byPassToken.getSecond())) {
                    return true;
                }
            }
        }
        return false;
    }
}
