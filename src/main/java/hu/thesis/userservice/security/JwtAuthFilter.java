//package hu.thesis.userservice.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.MDC;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private static final String AUTHORIZATION = "Authorization";
//    private static final String ACC_TOKEN = "acc_token";
//    private static final String BEARER = "Bearer ";
//    private final JwtTokenService jwtTokenService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String jwtToken = request.getHeader(AUTHORIZATION);
//
//        MDC.put(ACC_TOKEN, jwtToken);
//        UsernamePasswordAuthenticationToken authentication =  createUserDetailsFromAuthHeader(jwtToken, jwtTokenService);
//        if (authentication != null) {
//            MDC.put("username", authentication.getName());
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder
//                    .getContext()
//                    .setAuthentication(authentication);
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    public static UsernamePasswordAuthenticationToken createUserDetailsFromAuthHeader(String authHeader, JwtTokenService jwtService) {
//        if(authHeader != null && authHeader.startsWith(BEARER)) {
//            String jwtToken = authHeader.substring(BEARER.length());
//            UserDetails principal = jwtService.parseJwt(jwtToken);
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//            return authentication;
//        }
//        return null;
//    }
//
//}
