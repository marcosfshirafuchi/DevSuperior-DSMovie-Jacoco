package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String existingUsername;
	private UserEntity userEntity;

	@BeforeEach
	void setUp(){
		//Inicializar as variaveis
		existingUsername = "maria@gmail.com";
		//Criar um usuário
		userEntity =UserFactory.createUserEntity();

		//Mockar os métodos abaixo da classe UserService
		//Esse mock é do teste public void authenticatedShouldReturnUserEntityWhenUserExists()
		Mockito.lenient().when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(userEntity));
	}


	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		//Obtem o token do usuario logado e então retonar o usuário
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		//Resultado do método authenticated do UserService
		UserEntity result = service.authenticated();

		//Verificar se o resultado não é nulo
		Assertions.assertNotNull(result);
		//Verifica o username do usuário
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
	}
}
