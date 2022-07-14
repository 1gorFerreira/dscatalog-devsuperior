package com.devsuperior.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	//Environment é o ambiente de execução da sua aplicação. A partir desse objeto você consegue acessar várias variavéis interessantes;
	@Autowired
	private Environment env;
	
	//É necessário o tokenStore para configurar nos métodos;
	@Autowired
	private JwtTokenStore tokenStore;
	
	//Constantes para usar na configuração (Por organização)
	//OBS: /** -> Todo mundo a partir da rota;
	
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };//Quem vão ser os endpoints públicos?;

	private static final String[] OPERATOR_OR_ADMIN = { "/products/**", "/categories/**"}; //Quais rotas por padrão estarão liberadas tanto para operator quanto pra admin?;
	
	private static final String[] ADMIN = {"/users/**"};
	
	//Método que usaremos para configurar o tokenStore;
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore); //Com isso nosso resourceServer será capaz de decodificar o token e analizar se o token tá batendo com o secret, expirado ou não, etc;
	}

	//Método para configurar as rotas;
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		//Para liberar os frames do H2:
		if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")//Estou dizendo que a rota do vetor OPERATOR_OR_ADMIN podem ser acessadas por quem for OPERATOR ou ADMIN;
		.antMatchers(ADMIN).hasAnyRole("ADMIN")
		.anyRequest().authenticated();//Quem for acessar qualquer outra rota não especificada anteriormente, precisa estar logado. Não importando perfil de usuário;
	
		http.cors().configurationSource(corsConfigurationSource());
	}

	//CORS;
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration corsConfig = new CorsConfiguration();
	    corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
	    corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
	    corsConfig.setAllowCredentials(true);
	    corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
	 
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", corsConfig);
	    return source;
	}
	 
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
	    FilterRegistrationBean<CorsFilter> bean
	            = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
	    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    return bean;
	}

	
}
