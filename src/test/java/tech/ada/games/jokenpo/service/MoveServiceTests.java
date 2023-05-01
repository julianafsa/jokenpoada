package tech.ada.games.jokenpo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import tech.ada.games.jokenpo.dto.MoveDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.repository.MoveRepository;

public class MoveServiceTests {

    private final MoveRepository moveRepository = Mockito.mock(MoveRepository.class);

    private final MoveService moveService = new MoveService(moveRepository);

    @Test
	void testCreateMove(){
		
    	MoveDto moveDto = new MoveDto("papel");
		
		when(moveRepository.existsByMove(anyString())).thenReturn(Boolean.FALSE);
		assertDoesNotThrow(() -> moveService.createMove(moveDto));
		
		verify(moveRepository, times(1)).save(any());
	}
	
	@Test
	void testCreateMoveDataConflictException(){
		
		MoveDto moveDto = new MoveDto("papel");
		
		when(moveRepository.existsByMove(anyString())).thenReturn(Boolean.TRUE);
    	
		DataConflictException e = assertThrows(DataConflictException.class,() -> moveService.createMove(moveDto), "se existir movimento cadastrado com o mesmo nome, o método deve lançar uma exceção");
    	assertEquals("A jogada já está cadastrada!", e.getMessage(),"a mensagem para a exceção deve ser.... A jogada já está cadastrada!");
    	verify(moveRepository, times(1)).existsByMove(anyString());
	}

	@Test
	void testCreateMoveBadRequestException(){
		
		MoveDto moveDto = new MoveDto("Serrote");
		
		when(moveRepository.existsByMove(anyString())).thenReturn(Boolean.FALSE);
    	
		BadRequestException e = assertThrows(BadRequestException.class,() -> moveService.createMove(moveDto), "se o usuário tentar cadastrar uma jogada inválida, o método deve lançar uma exceção");
    	assertEquals("Você pode cadastrar apenas os movimentos Spock, Tesoura, Papel, Pedra e Lagarto", e.getMessage(),"a mensagem para a exceção deve ser.... Você pode cadastrar apenas os movimentos Spock, Tesoura, Papel, Pedra e Lagarto");
    	verify(moveRepository, times(1)).existsByMove(anyString());
	}
    
    @Test
	void testFindMoves() throws DataNotFoundException {
    	
    	when(moveRepository.findAll()).thenReturn(List.of(new Move()));
    	
    	List<Move> list = moveService.findMoves();
    	
    	assertFalse(list.isEmpty(),"A lista não pode estar vazia se existir movimentos cadastrados");
    	
    }

    
	@Test
	void testFindMovesDataNotFoundException(){
    	
    	when(moveRepository.findAll()).thenReturn(Collections.emptyList());
    	
    	DataNotFoundException e = assertThrows(DataNotFoundException.class,() -> moveService.findMoves(), "se não existir movimentos cadastrados, o método deve lançar uma exceção");
    	assertEquals("Não há jogadas cadastradas!", e.getMessage(),"a mensagem para a exceção deve ser.... Não há jogadas cadastradas!");
    	verify(moveRepository, times(1)).findAll();
    }

    
    @Test
	void testFindByMove() throws DataNotFoundException {
    	
		String moveName = "papel";
		
		Move move = new Move(12l, "papel", null);
		
    	when(moveRepository.findByMove(moveName)).thenReturn(Optional.of(move));
    	
    	Move moveRet = moveService.findByMove(moveName);
    	
    	assertEquals(move, moveRet, "Movimento cadastrado deve ser retornado");
    }
	
	@Test
	void testFindByMoveDataNotFoundException(){
    	
		String moveName = "serrote";
    	when(moveRepository.findByMove(moveName)).thenReturn(Optional.ofNullable(null));
    	
    	DataNotFoundException e = assertThrows(DataNotFoundException.class,() -> moveService.findByMove(moveName), "se a jpgada não estiver cadastrada, o método deve lançar uma exceção");
    	assertEquals("A jogada não está cadastrada!", e.getMessage(),"a mensagem para a exceção deve ser.... A jogada não está cadastrada!");
    	verify(moveRepository, times(1)).findByMove(moveName);
    }

}
