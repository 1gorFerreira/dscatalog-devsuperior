package com.devsuperior.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.devsuperior.dscatalog.components.JwtTokenEnhancer;

@Configuration
@EnableAuthorizationServer //Anotation que faz o processamento por debaixo dos panos para dizer que essa classe é que vai representar o authorizationServer do oauth;
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{

	//4 Beans que iremos precisar (Podemos encontrar suas implementações nas classes de config);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	//Variáveis de ambiente:
	
	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	@Value("${jwt.duration}")
	private Integer jwtDuration;
	
	@Autowired
	private JwtTokenEnhancer tokenEnhancer;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	//ClientDetailsServiceConfigurer -> é nesse objeto que iremos definir como será nossa autenticação e quais vão ser os dados do cliente;
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory() //Para dizer que o processo será feito em memória;
		.withClient(clientId) //Para definir o clientId; Quando nossa aplicação for acessar o backend, ela vai ter que informar o nome dela (que é dscatalog);
		.secret(passwordEncoder.encode(clientSecret)) //Para definir o client secret (Senha da aplicação, não do usuário);
		.scopes("read", "write") //Para dizer que será um acesso de leitura, escrita, etc;
		.authorizedGrantTypes("password") //Tipos de acesso para login (Password, etc etc);
		.accessTokenValiditySeconds(jwtDuration); //Tempo de expiração do token;
	}

	//AuthorizationServerEndpointsConfigurer -> Configuração que dirá quem irá autorizar e qual vai ser o formato do token;
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		//Configurando o chain para aceitar o TokenEnhancer que fizemos;
		TokenEnhancerChain chain = new TokenEnhancerChain();
		chain.setTokenEnhancers(Arrays.asList(accessTokenConverter, tokenEnhancer));
		
		endpoints.authenticationManager(authenticationManager) //Quem vai processar/autorizar a autenticação? SPRINGSECURITY usando o authenticationManager;
		.tokenStore(tokenStore) //Quais vão ser os objetos responsáveis por processar os tokens? tokenStore;
		.accessTokenConverter(accessTokenConverter)
		.tokenEnhancer(chain);//Aplicando a Enhancer;
		
	}
	
}
