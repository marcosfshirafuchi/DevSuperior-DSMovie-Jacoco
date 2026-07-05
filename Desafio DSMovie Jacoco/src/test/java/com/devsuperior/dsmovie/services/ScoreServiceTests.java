package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ScoreServiceTests {

    @InjectMocks
    private ScoreService service;

    @Mock
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScoreRepository scoreRepository;

    //Cria as variaveis
    private ScoreDTO scoreDTO;
    private UserEntity userEntity;
    private MovieEntity movieEntity;
    private ScoreEntity scoreEntity;
    private long existingMovieId;
    private String existingTitle;
    private long nonExistingMovieId;

    //Inicializa as variaveis antes de começar os testes
    @BeforeEach
    void setUp() throws Exception {
        //Inicializar as variaveis
        scoreDTO = ScoreFactory.createScoreDTO();
        userEntity = UserFactory.createUserEntity();
        movieEntity = MovieFactory.createMovieEntity();
        scoreEntity = ScoreFactory.createScoreEntity();
        existingMovieId = 1L;
        existingTitle = "Test Movie";
        nonExistingMovieId = 2L;

        //Mockar os métodos abaixo da classe MovieService
        //Esse mock é do teste public void saveScoreShouldReturnMovieDTO()
        Mockito.lenient().when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
        Mockito.lenient().when(scoreRepository.saveAndFlush(any())).thenReturn(scoreEntity);
        Mockito.lenient().when(movieRepository.save(any())).thenReturn(movieEntity);

        //Esse mock é do teste public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId()
        Mockito.lenient().when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
    }

    @Test
    public void saveScoreShouldReturnMovieDTO() {
        //Chama o método authenticatred do userService e retorna o usuário criado
        Mockito.lenient().when(userService.authenticated()).thenReturn(userEntity);

        //Cria o score e preenche com os dados do score
        ScoreEntity score = new ScoreEntity();
        //Preenche os dados do movie
        score.setMovie(movieEntity);
        //Preenche os dados do user
        score.setUser(userEntity);
        //Obter Score do filme
        score.setValue(scoreDTO.getScore());
        //Adiciona o score ao filme
        movieEntity.getScores().add(score);

        //Resultado do método saveScore do service
        MovieDTO result = service.saveScore(scoreDTO);

        //Verificar se o resultado não é nulo
        Assertions.assertNotNull(result);
        //Verifica o id do filme
        Assertions.assertEquals(movieEntity.getId(), result.getId());
        //Verififica o resultado do Score do filme
        Assertions.assertEquals(scoreDTO.getScore(), result.getScore());
        //Verifica a quantidade de scores do filme
        Assertions.assertEquals(1, result.getCount());
    }

    @Test
    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

        // Usuário autenticado
        Mockito.when(userService.authenticated()).thenReturn(userEntity);

        // DTO apontando para um filme inexistente
        ScoreDTO dto = new ScoreDTO(nonExistingMovieId, 4.0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.saveScore(dto);
        });

        // Verifica se tentou buscar o filme inexistente
        Mockito.verify(movieRepository).findById(nonExistingMovieId);

        // Como o filme não existe, não deve salvar score
        Mockito.verify(scoreRepository, Mockito.never()).saveAndFlush(any());

        // Nem atualizar o filme
        Mockito.verify(movieRepository, Mockito.never()).save(any());
    }
}
