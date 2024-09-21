package app.cardcapture.auth.jwt.filter;

import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.exception.InvalidTokenException;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.dto.ErrorResponseDto;
import app.cardcapture.security.PrincipalDetails;
import app.cardcapture.security.PrincipalUserDetailsService;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtComponent jwtComponent;
    private final PrincipalUserDetailsService userDetailsService;
    private final ObjectWriter objectWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        log.info("authHeader = " + authHeader);
        log.info("requestURI = " + request.getRequestURI());

        try {
            if (authHeader != null && !authHeader.isEmpty()) {
                if (authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                String authToken = authHeader;
                Claims claims = jwtComponent.verifyAccessToken(authToken);
                if (claims != null) {
                    Long userId = claims.getId();
                    if (userId != null) {
                        PrincipalDetails userDetails = (PrincipalDetails) userDetailsService.loadUserByUsername(userId.toString());

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
        } catch (InvalidTokenException ex) {
            ErrorResponseDto<String> errorResponse = ErrorResponseDto.create(ErrorCode.INVALID_TOKEN);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectWriter.writeValueAsString(errorResponse));
        }
    }
}