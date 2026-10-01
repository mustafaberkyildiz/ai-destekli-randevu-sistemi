package com.mustafaberk.jwt;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String SECRET_KEY = "1C+noV8jaktjX38XOXZxnr9Eg2Zv7wbes+XmXMeRLgA=";
	
	public String generateToken(UserDetails userDetails) {
		


	    String role = userDetails.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .findFirst()
	            .orElseThrow( () -> new BaseException(new ErrorMessage(MessageType.ROLE_NOT_FOUND, null)) );




	    
		return Jwts.builder()
		.setSubject(userDetails.getUsername())
		.claim("role",role)
		.setIssuedAt(new Date())
		.setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
		.signWith(getKey(),SignatureAlgorithm.HS256)
		.compact();
		
	}
	
	public <T> T exportToken(String token , Function<Claims, T> claimsFunction) {
		Claims claims = getClaims(token);
		return claimsFunction.apply(claims);
	}
	
	public Claims getClaims(String token) { 
		Claims claims = Jwts.parserBuilder()
		.setSigningKey(getKey())
		.build()
		.parseClaimsJws(token).getBody();


		return claims;
	}
	
	public String getUsernameByToken(String token) {
		return exportToken(token, Claims::getSubject);
	}
	
	public boolean isTokenValid(String token) {
		Date expireDate = exportToken(token, Claims::getExpiration);// claims -> claims.getExpiration() aslinda boyle ama kisa yazimini yaziyoruz biz
		return expireDate.after(new Date());
	}
	
	public String getRoleByToken(String token) {
		return exportToken(token,  claims -> claims.get("role", String.class)); 

	}
	
	public Key getKey() {
		byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(bytes);	
	}
	
}
