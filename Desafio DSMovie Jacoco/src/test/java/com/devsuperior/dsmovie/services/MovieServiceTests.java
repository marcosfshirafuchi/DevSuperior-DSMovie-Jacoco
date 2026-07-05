package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
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
	private long nonExistingMovieId;
	private MovieDTO movieDTO;
	private long dependentMovietId;

	//Inicializa as variaveis antes de começar os testes
	@BeforeEach
	void setUp() throws Exception {
		//Inicializar as variaveis
		existingTitle = "Test Movie";
		movieEntity = MovieFactory.createMovieEntity();
		page = new PageImpl<>(List.of((movieEntity)));
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		movieDTO = MovieFactory.createMovieDTO();
		dependentMovietId = 3L;

		//Mockar os métodos abaixo da classe MovieService
		//Esse mock é do teste public void findAllShouldReturnPagedMovieDTO()
		Mockito.lenient().when(repository.searchByTitle(any(), (Pageable)any())).thenReturn(page);

		//Esse mock é do teste public void findByIdShouldReturnMovieDTOWhenIdExists()
		Mockito.lenient().when(repository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));

		//Esse mock é do teste public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist()
		Mockito.lenient().when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		//Esse mock é do teste public void insertShouldReturnMovieDTO()
		Mockito.lenient().when(repository.save(any())).thenReturn(movieEntity);

		//Esse mock é do teste public void updateShouldReturnMovieDTOWhenIdExists()
		Mockito.lenient().when(repository.getReferenceById(existingMovieId)).thenReturn(movieEntity);

		//Esse mock é do teste public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist()
		Mockito.lenient().when(repository.getReferenceById(nonExistingMovieId)).thenThrow(new EntityNotFoundException());

		//Esse mock é do teste public void deleteShouldDoNothingWhenIdExists()
		Mockito.lenient().when(repository.existsById(existingMovieId)).thenReturn(true);

		//Esse mock é do teste public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist()
		Mockito.lenient().when(repository.existsById(nonExistingMovieId)).thenReturn(false);

		//Esses mocks são do teste public void deleteShouldThrowDatabaseExceptionWhenDependentId()
		Mockito.lenient().when(repository.existsById(dependentMovietId)).thenReturn(true);
		//Para dar throw precisar chamar a classe de exceção DataIntegrityViolationException
		Mockito.lenient().doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentMovietId);
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
		//Resultado do método findById do service quando o id não existe
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			//Verifica o método findById da classe MovieService quando o id não existe
			service.findById(nonExistingMovieId);
		});
	}

	@Test
	public void insertShouldReturnMovieDTO() {
		//Resultado do método insert do service
		MovieDTO result = service.insert(new MovieDTO(movieEntity));

		//Verifica se o resultado não é nulo
		Assertions.assertNotNull(result);

		//Verifica o id do filme
		Assertions.assertEquals(result.getId(), movieEntity.getId());
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		//Resultado do método update do service
		MovieDTO result = service.update(existingMovieId, movieDTO);

		//Verifica se o resultado não é nulo
		Assertions.assertNotNull(result);

		//Verifica o id do filme
		Assertions.assertEquals(result.getId(), existingMovieId);

		//Verifica o nome do título do filme
		Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		//Resultado do método update do service
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			//Verifica o método update da classe MovieService quando o id não existe
			service.update(nonExistingMovieId, movieDTO);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		//Resultado do método delete do service, neste caso não retorna nada
		Assertions.assertDoesNotThrow(() ->{
			//Verifica o método delete da classe MovieService quando o id existe
			service.delete(existingMovieId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		//Resultado do método delete do service quando o id não existe
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			//Verifica o método delete da classe MovieService quando o id não existe
			service.delete(nonExistingMovieId);
		});
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		//Resultado do método delete do service da exceção DatabaseException quando o id é dependente
		Assertions.assertThrows(DatabaseException.class, ()->{
			//Verifica o método delete da classe MovieService quando o id é dependente
			service.delete(dependentMovietId);
		});
	}
}

