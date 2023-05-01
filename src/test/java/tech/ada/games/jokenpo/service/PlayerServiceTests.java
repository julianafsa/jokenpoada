package tech.ada.games.jokenpo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import tech.ada.games.jokenpo.dto.PlayerDto;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Player;
import tech.ada.games.jokenpo.model.Role;
import tech.ada.games.jokenpo.repository.PlayerMoveRepository;
import tech.ada.games.jokenpo.repository.PlayerRepository;
import tech.ada.games.jokenpo.repository.RoleRepository;

class PlayerServiceTests {

	private final PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
	private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	private final PlayerMoveRepository playerMoveRepository = Mockito.mock(PlayerMoveRepository.class);
	private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	private final PlayerService playerService = new PlayerService(playerRepository, roleRepository, playerMoveRepository, passwordEncoder);

	
	@Test
	void testCreatePlayer(){
		
		PlayerDto player = new PlayerDto("adaTest","password","ada");
		
		when(playerRepository.existsByUsername(anyString())).thenReturn(Boolean.FALSE);
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role()));
		assertDoesNotThrow(() -> playerService.createPlayer(player));
		
		verify(playerRepository, times(1)).existsByUsername(player.getUsername());
		verify(passwordEncoder, times(1)).encode(any());
		verify(roleRepository, times(1)).findByName("ROLE_USER");
		verify(playerRepository, times(1)).save(any());
	}
	
	@Test
	void testCreatePlayerDataConflictException(){
		
		PlayerDto player = new PlayerDto("adaTest","password","ada");
		
		when(playerRepository.existsByUsername(anyString())).thenReturn(Boolean.TRUE);
    	
		DataConflictException e = assertThrows(DataConflictException.class,() -> playerService.createPlayer(player), "se existir usuário cadastrado com o mesmo nome, o método deve lançar uma exceção");
    	assertEquals("O jogador já está cadastrado!", e.getMessage(),"a mensagem para a exceção deve ser.... O jogador já está cadastrado!");
    	verify(playerRepository, times(1)).existsByUsername(anyString());
	}
	
	@Test
	void testFindPlayers() throws DataNotFoundException {
    	
    	when(playerRepository.findAll()).thenReturn(List.of(new Player()));
    	
    	List<Player> list = playerService.findPlayers();
    	
    	assertFalse(list.isEmpty(),"A lista não pode estar vazia se existir usuários cadastrados");
    	
    }
	
	
	@Test
	void testFindPlayersDataNotFoundException(){
    	
    	when(playerRepository.findAll()).thenReturn(Collections.emptyList());
    	
    	DataNotFoundException e = assertThrows(DataNotFoundException.class,() -> playerService.findPlayers(), "se não existir usuários cadastrados, o método deve lançar uma exceção");
    	assertEquals("Não há jogadores cadastrados!", e.getMessage(),"a mensagem para a exceção deve ser.... Não há jogadores cadastrados!");
    	verify(playerRepository, times(1)).findAll();
    }

	@Test
	void testFindByPlayer() throws DataNotFoundException {
    	
		String userName = "adaTest";
		
		Player player = new Player(321l,userName, null,null,null);
		
    	when(playerRepository.findByUsername(userName)).thenReturn(Optional.of(player));
    	
    	Player playerRet = playerService.findByPlayer(userName);
    	
    	assertEquals(player, playerRet, "Usuário cadastrado deve ser retornado");
    }
	
	@Test
	void testFindByPlayerDataNotFoundException(){
    	
		String playerName = "name";
    	when(playerRepository.findByUsername(playerName)).thenReturn(Optional.ofNullable(null));
    	
    	DataNotFoundException e = assertThrows(DataNotFoundException.class,() -> playerService.findByPlayer(playerName), "se não houver usuário com o nome utilizado no filtro, o método deve lançar uma exceção");
    	assertEquals("O jogador não está cadastrado!", e.getMessage(),"a mensagem para a exceção deve ser.... O jogador não está cadastrado!");
    	verify(playerRepository, times(1)).findByUsername(playerName);
    }


	@Test
	void testDeletePlayer(){
    	
		Long playerID = 321l;
		
		Player player = new Player(321l,null, null,null,null);
		
		doNothing().when(playerRepository).delete(player);
		
		when(playerRepository.findById(playerID)).thenReturn(Optional.of(player));
    	when(playerMoveRepository.countByUnfinishedGameAndPlayer(playerID)).thenReturn(0l);
    	
    	assertDoesNotThrow(() -> playerService.deletePlayer(playerID));
    	verify(playerRepository, times(1)).findById(playerID);
    	verify(playerMoveRepository, times(1)).countByUnfinishedGameAndPlayer(playerID);
    	verify(playerRepository, times(1)).delete(player);
    	
    }
	
	@Test
	void testDeletePlayerDataNotFoundException(){
    	
		Long playerID = 321l;
		
		Player player = new Player(321l,null, null,null,null);
		
		doNothing().when(playerRepository).delete(player);
		
		when(playerRepository.findById(playerID)).thenReturn(Optional.ofNullable(null));
		
		DataNotFoundException e = assertThrows(DataNotFoundException.class,() -> playerService.deletePlayer(playerID), "se não houver usuário com o nome utilizado no filtro, o método deve lançar uma exceção");
    	assertEquals("O jogador não está cadastrado!", e.getMessage(),"a mensagem para a exceção deve ser.... O jogador não está cadastrado!");
    	verify(playerRepository, times(1)).findById(playerID);
		
    }

	
	@Test
	void testDeletePlayerDataConflictException(){
    	
		Long playerID = 321l;
		
		Player player = new Player(321l,null, null,null,null);
		
		doNothing().when(playerRepository).delete(player);
		
		when(playerRepository.findById(playerID)).thenReturn(Optional.of(player));
		when(playerMoveRepository.countByUnfinishedGameAndPlayer(playerID)).thenReturn(1l);
		
		DataConflictException e = assertThrows(DataConflictException.class,() -> playerService.deletePlayer(playerID), "se não houver usuário com o nome utilizado no filtro, o método deve lançar uma exceção");
    	assertEquals("O jogador está registrado em uma partida não finalizada!", e.getMessage(),"a mensagem para a exceção deve ser.... O jogador está registrado em uma partida não finalizada!");
    	verify(playerMoveRepository, times(1)).countByUnfinishedGameAndPlayer(playerID);
		
    }

}
