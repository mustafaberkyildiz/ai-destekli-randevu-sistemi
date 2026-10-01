package com.mustafaberk.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{ 



	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		if(header==null || header.trim().isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token;
		String tc;
		
		token = header.substring(7);
		
		try {
			tc = jwtService.getUsernameByToken(token);
			if(tc!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(tc); 

				if(userDetails!=null && jwtService.isTokenValid(token)) {
					UsernamePasswordAuthenticationToken authenticationToken = new 
					UsernamePasswordAuthenticationToken(tc, null, userDetails.getAuthorities());
					
					authenticationToken.setDetails(userDetails);
					
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		}
		catch (ExpiredJwtException ex) {
			throw new BaseException(new ErrorMessage(MessageType.TOKEN_IS_EXPIRED, ex.getMessage()));
		}
		catch (Exception ex) {
			throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, ex.getMessage()));
		}
		
		filterChain.doFilter(request, response);
		
	}

	
}
