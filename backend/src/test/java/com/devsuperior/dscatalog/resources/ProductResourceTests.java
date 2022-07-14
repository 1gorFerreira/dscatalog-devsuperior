package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.factory.Factory;
import com.devsuperior.dscatalog.tests.factory.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	 //Nesse caso não tem problema usar o Autowired (Injetar de verdade o objeto) pois o objectMapper é só um objeto auxiliar e não uma dependencia do productResource, não ferindo o teste da unidade;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private PageImpl<ProductDTO> page;
	private ProductDTO productDto;
	
	private String username;
	private String password;
	
	@BeforeEach
	void setUp() throws Exception {
		
		username = "maria@gmail.com";
		password = "123456";
		
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		productDto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDto)); 
		
		when(service.findAllPaged(any(), any(), any())).thenReturn(page);
		
		when(service.findById(existingId)).thenReturn(productDto);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		//Quando usamos o any() os outros parametros não podem ser simples;
		when(service.update(eq(existingId), any())).thenReturn(productDto);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(any())).thenReturn(productDto);
		
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
		
	}
	
	@Test
	public void findAllPagedShouldReturnPage() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		//Assertions
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));// Tipo de dados da resposta;
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());//Cifrão vai acessar o objeto da resposta;
		result.andExpect(jsonPath("$.name").exists()); 
		result.andExpect(jsonPath("$.description").exists()); 
		
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.id").doesNotExist());
		result.andExpect(jsonPath("$.name").doesNotExist());
		result.andExpect(jsonPath("$.description").doesNotExist());
	}
	
	@Test
	public void updateShouldReturnProductWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		//Convertendo o objeto Java productDto para String;
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody) //Passando o jsonBody na requisição;
						.contentType(MediaType.APPLICATION_JSON) //Tipo de dados da requisição;
						.accept(MediaType.APPLICATION_JSON)); // Tipo de dados da resposta;
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists()); 
		result.andExpect(jsonPath("$.description").exists()); 
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody) //Passando o jsonBody na requisição;
						.contentType(MediaType.APPLICATION_JSON) //Tipo de dados da requisição;
						.accept(MediaType.APPLICATION_JSON)); // Tipo de dados da resposta;
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void insertShouldReturnProductCreated() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		ResultActions result = 
				mockMvc.perform(post("/products")
						.header("Authorization", "Bearer " + accessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").value(productDto.getId()));
		result.andExpect(jsonPath("$.name").value(productDto.getName())); 
		result.andExpect(jsonPath("$.description").value(productDto.getDescription())); 		
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions result =
				mockMvc.perform(delete("/products/{id}", existingId)
						.header("Authorization", "Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions result =
				mockMvc.perform(delete("/products/{id}", nonExistingId)
						.header("Authorization", "Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
}
