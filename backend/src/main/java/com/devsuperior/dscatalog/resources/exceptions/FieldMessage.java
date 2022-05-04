package com.devsuperior.dscatalog.resources.exceptions;

import java.io.Serializable;

public class FieldMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//Nome do Error, Mensagem;
	//Ex: Name, Tem que ser de 5 a 60 caracteres;
	private String fieldName;
	private String message;
	
	public FieldMessage() {
		
	}

	public FieldMessage(String fieldName, String message) {
		this.fieldName = fieldName;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
