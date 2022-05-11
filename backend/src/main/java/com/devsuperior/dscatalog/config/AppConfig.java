package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/*
 * Classe que guarda configurações do aplicativo como um todo. 
 * Dentro de um projeto Spring podemos ter classes de configuração que 
 * serão responsaveis por manter uma configuração, um componente especifico e etc.
 * (@Configuration)
 */

@Configuration
public class AppConfig {
	
	/*
	 * Componente do Spring assim como o service por exemplo.
	 * Colocando o @Bean estamos dizendo que essa instancia será gerenciado pelo SpringBoot.
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//Beans para JWT: Configuração do JWTtoken, esses 2 Beans são objetos que serão capazes de acessar um token JWT (Ler, criar, etc);
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey("MY-JWT-SECRET");
		return tokenConverter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
}
