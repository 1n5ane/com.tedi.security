package com.tedi.security.filters

import com.tedi.security.domains.User
import com.tedi.security.services.JwtService
import com.tedi.security.services.UserDetailsServiceImpl
import groovy.util.logging.Slf4j
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.context.SecurityContextHolder

@Component
@Slf4j
class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserDetailsServiceImpl userDetailsService

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {

            String jwt = authHeader.substring("Bearer ".size())

            String username = jwtService.extractUsername(jwt)

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication()

            if (username != null && authentication == null) {
                User user = userDetailsService.loadUserByUsername(username) as User

                if (jwtService.isTokenValid(jwt, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
                    SecurityContextHolder.getContext().setAuthentication(authToken)
                }
            }
        } catch (ExpiredJwtException expiredJwtException) {
            log.trace("Token has expired: ${expiredJwtException.getMessage()}")
        } catch (MalformedJwtException malformedJwtException) {
            log.trace("Token is malformed: ${malformedJwtException.getMessage()}")
        } catch (SignatureException signatureException) {
            log.trace("Jwt signature couldn't be verified: ${signatureException.getMessage()}")
        }
        filterChain.doFilter(request, response)
    }
}