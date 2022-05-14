package com.devsuperior.dscatalog.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;

//Classe para acrescentar informações no Token;

@Component
public class JwtTokenEnhancer implements TokenEnhancer{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		User user = userRepository.findByEmail(authentication.getName());//.getName() vai buscar por usuário que no nosso caso é o email;
		
		Map<String, Object> map = new HashMap<>();
		map.put("userFirstName", user.getFirstName());
		map.put("userId", user.getId());
		
		//Para inserir no token teremos que acessar o objeto acessToken, porém o método para inserir não está no tipo OAuth2AccessToken então acessaremos um mais especifico:
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken; //Fizemos o Downcast pois o tipo DefaultOAuth2AccessToken que contém o método de inserir as info adicionais;
		token.setAdditionalInformation(map);
		
		return accessToken;//Estou retornando o mesmo token porém com mais informações nele;
	}
	
}
