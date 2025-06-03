package com.mingxoop.sandbox.global.security;

import java.util.Collection;
import java.util.List;

import com.mingxoop.sandbox.domain.user.repository.entity.Role;
import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppUserDetails extends UserEntity implements UserDetails {

	private Long id;
	private String email;
	private String password;
	private Role role;

	private AppUserDetails(Long id, String email, Role role) {
		this.id = id;
		this.email = email;
		this.role = role;
	}

	public static AppUserDetails of(Long id, String email, String role) {
		return new AppUserDetails(id, email, Role.valueOf(role));
	}

	public static AppUserDetails of(UserEntity user) {
		return new AppUserDetails(user.getId(), user.getEmail(), user.getRole());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.getAuthority()));
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}