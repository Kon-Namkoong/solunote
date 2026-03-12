package com.vol.solunote.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.vol.solunote.model.vo.siteuser.SiteUser;
import com.vol.solunote.repository.siteuser.SiteUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final String ROLE_PREFIX = "ROLE_";
	
	@Autowired
	SiteUserRepository siteuserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		SiteUser member = siteuserRepository.readUser(username);
		if(member != null && member.getUsername() != null && !"".equals(member.getUsername()) ) {
			
			List<String> list = null;
			
			list = siteuserRepository.readAuthority(username);
			
			if(list == null ) list = new ArrayList<>();
			
			if(!list.isEmpty()) {
				member.setMoveURL(ROLE_PREFIX+list.get(0));
			}

			member.setAuthorities(makeGrantedAuthority(list));
			return new SecurityMember(member);
		}else {
			throw new UsernameNotFoundException(username);
		}
	}
	
	private static List<GrantedAuthority> makeGrantedAuthority(List<String> roles){
		List<GrantedAuthority> list = new ArrayList<>();
		roles.forEach(role -> list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
		return list;
	}
}
