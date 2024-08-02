package app.cardcapture.auth.jwt.filter;

import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.security.PrincipleDetails;
import app.cardcapture.security.PrincipleUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtComponent jwtComponent;
    private final PrincipleUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader = " + authHeader);
        System.out.println("requestURI = " + request.getRequestURI());

        if (authHeader != null && !authHeader.isEmpty()) {
            if (authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
            String authToken = authHeader;
            Claims claims = jwtComponent.verifyAccessToken(authToken);
            if (claims != null) {
                Long userId = claims.getId();
                if (userId != null) {
                    PrincipleDetails userDetails = (PrincipleDetails) userDetailsService.loadUserByUsername(userId.toString());

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        System.out.println("UserDetails is null for userId: " + userId);
                    }
                } else {
                    System.out.println("UserId is null in claims");
                }
            } else {
                System.out.println("Claims is null for token: " + authToken);
            }
        }
        chain.doFilter(request, response);
    }
}
