package com.demo.user.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.demo.user.token.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final HandlerExceptionResolver handlerExceptionResolver;

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
			HandlerExceptionResolver handlerExceptionResolver) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String jwt = authHeader.substring(7);
			final String userEmail = jwtService.extractUsername(jwt);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (userEmail != null && authentication == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

				if (jwtService.isTokenValid(jwt, userDetails)) {
					String role = (String) jwtService.extractAllClaims(jwt).get("role");

					List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, authorities);

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			filterChain.doFilter(request, response);
		} catch (Exception exception) {
			handlerExceptionResolver.resolveException(request, response, null, exception);
		}
	}

}

//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final HandlerExceptionResolver handlerExceptionResolver;
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
//                                   HandlerExceptionResolver handlerExceptionResolver) {
//        this.jwtService = jwtService;
//        this.userDetailsService = userDetailsService;
//        this.handlerExceptionResolver = handlerExceptionResolver;
//    }
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request, 
//    		                        @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7);
//            final String userEmail = jwtService.extractUsername(jwt);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (userEmail != null && authentication == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    // Extract roles as a list (assumes roles are stored in the token as a List or String[])
//                    List<String> roles = (List<String>) jwtService.extractAllClaims(jwt).get("roles");
//
//                    // If roles is null or empty, throw an exception or handle accordingly
//                    if (roles == null || roles.isEmpty()) {
//                        throw new IllegalArgumentException("User roles are missing in the JWT.");
//                    }
//
//                    // Convert the list of roles into a list of GrantedAuthority
//                    List<GrantedAuthority> authorities = roles.stream()
//                            .map(SimpleGrantedAuthority::new)
//                            .collect(Collectors.toList());
//
//                    // Create authentication token with the user's roles
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, authorities);
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//
//            filterChain.doFilter(request, response);
//        } catch (Exception exception) {
//            handlerExceptionResolver.resolveException(request, response, null, exception);
//        }
//    }
//}
