package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class MovieServiceTests {

	//Vai fazer a injenção de dependencia na classe MovieService
	@InjectMocks
	private MovieService service;

	//Mockar a classe MovieRepository
	@Mock
	private MovieRepository repository;

	//Criar as variaveis
	private String existingTitle;
	private MovieEntity movieEntity;
	//PageImpl<MovieEntity> representa o Page<MovieDTO> do MovieService
	private PageImpl<MovieEntity> page;
	private long existingMovieId;

	//Inicializa as variaveis antes de começar os testes
	@BeforeEach
	void setUp() throws Exception {
		//Inicializar as variaveis
		existingTitle = "Test Movie";
		movieEntity = MovieFactory.createMovieEntity();
		page = new PageImpl<>(List.of((movieEntity)));
		existingMovieId = 1L;

		//Mockar os métodos abaixo da classe MovieService
		//Esse mock é do teste public void findAllShouldReturnPagedMovieDTO()
		Mockito.lenient().when(repository.searchByTitle(any(), (Pageable)any())).thenReturn(page);

		//Esse mock é do teste public void findByIdShouldReturnMovieDTOWhenIdExists()
		Mockito.lenient().when(repository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		//Criar um pageable padrão
		Pageable pageable = PageRequest.of(0,12);
		//Mockito.when(repository.searchByTitle(any(), (Pageable)any())).thenReturn(page);
		//Resultado do método findAll do service
		Page<MovieDTO> result = service.findAll(existingTitle,pageable);

		//Verificar se o resultado não é nulo
		Assertions.assertNotNull(result);
		//Verifica o total de elementos
		Assertions.assertEquals(result.getTotalElements(),1);
		//Verifica o nome do título do filme
		Assertions.assertEquals(result.iterator().next().getTitle(),existingTitle);
	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		//Resultado do método findById do service quando o id existe
		MovieDTO result = service.findById(existingMovieId);

		//Verificar se o resultado não é nulo
		Assertions.assertNotNull(result);

		//Verifica o id do filme
		Assertions.assertEquals(result.getId(),existingMovieId);

		//Verifica o nome do título do filme
		Assertions.assertEquals(result.getTitle(),movieEntity.getTitle());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void insertShouldReturnMovieDTO() {
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}

