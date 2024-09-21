package app.cardcapture.auth.jwt.filter;

import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.dto.ErrorResponseDto;
import app.cardcapture.security.PrincipalDetails;
import app.cardcapture.security.PrincipalUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        logRequestDetails(request);

        if (isAuthHeaderMissing(request)) {
            chain.doFilter(request, response);
            return;
        }

        String authToken = extractAuthTokenFromRequest(request);
        Claims claims = verifyToken(authToken);

        if (claims == null) {
            handleInvalidToken(response);
            return;
        }

        processAuthenticationFromClaims(request, claims, authToken);
        chain.doFilter(request, response);
    }

    private void logRequestDetails(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("authHeader = " + authHeader);
        log.info("requestURI = " + request.getRequestURI());
    }

    private boolean isAuthHeaderMissing(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader == null || authHeader.isEmpty();
    }

    private String extractAuthTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return extractAuthToken(authHeader);
    }

    private Claims verifyToken(String authToken) {
        return jwtComponent.verifyAccessToken(authToken);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.create(
            ErrorCode.INVALID_TOKEN);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String extractAuthToken(String authHeader) {
        authHeader = extractRawAuthHeader(authHeader);
        String authToken = authHeader;
        return authToken;
    }

    private String extractRawAuthHeader(String authHeader) {
        if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }
        return authHeader;
    }

    private void processAuthenticationFromClaims(HttpServletRequest request, Claims claims,
        String authToken) {
        if (claims == null) {
            log.warn("Claims is null for token: " + authToken);
            return;
        }

        Long userId = claims.getId();
        authenticateUserById(request, userId);
    }

    private void authenticateUserById(HttpServletRequest request, Long userId) {
        if (userId == null) {
            log.warn("UserId is null in claims");
            return;
        }

        PrincipalDetails userDetails = (PrincipalDetails) userDetailsService.loadUserByUsername(
            userId.toString());
        setAuthenticationInSecurityContext(request, userDetails, userId);
    }

    private void setAuthenticationInSecurityContext(HttpServletRequest request,
        PrincipalDetails userDetails, Long userId) {
        if (userDetails == null) {
            log.warn("UserDetails is null for userId: " + userId);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}