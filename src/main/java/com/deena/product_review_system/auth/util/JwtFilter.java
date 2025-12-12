package com.deena.product_review_system.auth.util;

import com.deena.product_review_system.auth.Repository.UserRepository;
import com.deena.product_review_system.auth.model.User;
import com.deena.product_review_system.auth.services.CustomUserDetailsServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsServices userDetailsServices;
    private final UserRepository userRepository;

    // RECOMMENDED: Skip the filter for public paths handled by SecurityConfig
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Skip for authentication paths, as they don't need a token
        return request.getServletPath().startsWith("/api/v1/auth");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt;
        String username;

        // 1. Check Header (Crucial fix: ensure space after "Bearer")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extract token (after "Bearer ")
        username = jwtService.extractUsername(jwt);
        User owner = userRepository.findByEmail(username).orElseThrow();

        // 2. Check extracted username and if user is already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsServices.loadUserByUsername(username);

            // 3. Validate Token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 4. Create Authentication Token
                UsernamePasswordAuthenticationToken authenticationFilter = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // Authorities contain roles and permissions
                );

                authenticationFilter.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Set Authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationFilter);
            }
        }

        filterChain.doFilter(request, response);
    }
}
