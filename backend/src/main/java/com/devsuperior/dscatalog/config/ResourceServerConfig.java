package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	
	//É necessário o tokenStore para configurar nos métodos;
	@Autowired
	private JwtTokenStore tokenStore;
	
	
	//Constantes para usar na configuração (Por organização)
	//OBS: /** -> Todo mundo a partir da rota;
	
	private static final String[] PUBLIC = { "/oauth/token" };//Quem vão ser os endpoints públicos?;

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
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")//Estou dizendo que a rota do vetor OPERATOR_OR_ADMIN podem ser acessadas por quem for OPERATOR ou ADMIN;
		.antMatchers(ADMIN).hasAnyRole("ADMIN")
		.anyRequest().authenticated();//Quem for acessar qualquer outra rota não especificada anteriormente, precisa estar logado. Não importando perfil de usuário;
	}

	
}
