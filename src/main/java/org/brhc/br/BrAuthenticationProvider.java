package org.brhc.br;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;

public class BrAuthenticationProvider implements AuthenticationProvider {
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	private BrUserDetailService userDetailsService;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
		UserDetails user = userDetailsService.loadUserByUsername(username);
//		密码1
		String pwd = user.getPassword();
//		密码2
		String presentedPassword = authentication.getCredentials().toString();
//		比较密码，不对的话，抛出错误！
		if(false){
			throw new BadCredentialsException("登录验证失败！");
		}
		
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				user, authentication.getCredentials(),
				authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());

		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication));
	}

	public GrantedAuthoritiesMapper getAuthoritiesMapper() {
		return authoritiesMapper;
	}

	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
	}

	public BrUserDetailService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(BrUserDetailService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	 

}
