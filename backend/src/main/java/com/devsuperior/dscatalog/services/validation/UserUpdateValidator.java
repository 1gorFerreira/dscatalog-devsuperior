package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

//													(Tipo da annotation / Tipo da classe que recebe esse annotation);
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	
	/*Usaremos esse objeto pois ele guarda as informações da requisição, então a partir dele conseguimos pegar o 
	código que veio na requisição de update. (Nesse caso o ID do objeto que está sendo atualizado);*/
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		//Para pegar o código passado na requisição (Id):
		//Handler... : Valor especial que acessa as variaveis da URL;
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userId = Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		
		User user = userRepository.findByEmail(dto.getEmail());
		
		if (user != null && userId != user.getId()) {
			list.add(new FieldMessage("email", "Email já existe!"));
		}
		
		//Está inserindo os erros na lista de errors do beans validation // Inverso do que é feito no ResourceExceptionHandler validation;
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}