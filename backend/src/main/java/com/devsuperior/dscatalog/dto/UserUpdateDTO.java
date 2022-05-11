package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid // Essa anottation que vai processar por debaixo dos panos a verificação se o email que inserimos já existe no banco;
public class UserUpdateDTO extends UserDTO{
	private static final long serialVersionUID = 1L;

	
}
