package com.mustafaberk.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mustafaberk.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails{
	
	@Column(name = "tc", unique = true, nullable = false)
	private String tc;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "surname")
	private String surname;
	
	@Column(name = "email" , unique = true)
	private String email;
	
	@Column(name = "phone" , unique = true)
	private String phone;
	
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;
	



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {


		return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name())); 

	}

	@Override
	public String getUsername() {
		return this.tc; 
	}
/*
Spring Security login yaparken şunu kullanır : getUsername()
👉 Ama burada “username” kelimesi isim anlamında değil,
👉 login identifier (giriş anahtarı) anlamına gelir.

🔥 SEN NE YAPIYORSUN?
Sen diyorsun ki:
“Ben sisteme TC ile gireceğim”

getUsername() → login için kullanılan değer

@Override
public String getUsername() {
    return this.tc;
}
👉 demek oluyor ki:
“Spring Security, kullanıcı adı olarak TC’yi kullan”

🔥 TEK CÜMLE

getUsername() metodunda TC döndürmemizin sebebi, Spring Security’nin login işlemlerinde “username”i kullanıcıyı bulmak için ana anahtar olarak kullanması ve 
bizim sistemde bu anahtarın TC olmasıdır.

Spring Security şunu çağırır:

loadUserByUsername("12345678901")

👉 yani username parametresi = TC olur

Spring Security’de:

“username = sadece bir string identifier”

Bu string:

username olabilir
email olabilir
TC olabilir ✔
phone olabilir
*/

}
