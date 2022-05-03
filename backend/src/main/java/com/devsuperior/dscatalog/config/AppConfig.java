package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
}
