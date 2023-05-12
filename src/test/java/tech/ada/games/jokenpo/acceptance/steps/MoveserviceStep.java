package tech.ada.games.jokenpo.acceptance.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.mockito.Mockito;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.repository.MoveRepository;
import tech.ada.games.jokenpo.service.MoveService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoveserviceStep {

    private final MoveRepository moveRepository;
    private final MoveService moveService;
    private List<Move> moves;
    private Move moveInput;
    private Move moveResponse;
    private DataNotFoundException exception;

    public MoveserviceStep() {
        this.moveRepository = Mockito.mock(MoveRepository.class);
        this.moveService = new MoveService(moveRepository);
    }


    @Dado("Uma lista vazia de jogadas")
    public void dado_uma_lista_vazia_de_jogadas() {
        moves = new ArrayList<>();
    }

    @Quando("Realiza uma busca de todas as jogadas")
    public void realiza_uma_busca_de_todas_as_jogadas() throws DataNotFoundException {
        Mockito.when(moveRepository.findAll()).thenReturn(List.of(new Move()));
        moves = moveService.findMoves();
    }

    @Entao("A lista de jogadas eh exibida")
    public void lista_de_jogadas_eh_exibida() {
        assertFalse(moves.isEmpty(),"A lista não pode estar vazia se existir movimentos cadastrados");
    }

    @Dado("Exibindo erro se nao ha jogadas cadastradas")
    public void dado_uma_lista_de_jogadas() {
        moves = new ArrayList<>();
    }

    @Quando("Realiza uma busca de todas as jogadas invalidas")
    public void realiza_uma_busca_de_jogadas_invalidas() {
        when(moveRepository.findAll()).thenReturn(Collections.emptyList());
        exception = assertThrows(DataNotFoundException.class,() -> moveService.findMoves(), "se não existir movimentos cadastrados, o método deve lançar uma exceção");
    }

    @Entao("Um erro eh lancado")
    public void lista_de_jogadas_nao_eh_exibida_erro_lancado() {
        assertEquals("Não há jogadas cadastradas!", exception.getMessage(),"a mensagem para a exceção deve ser.... Não há jogadas cadastradas!");
        verify(moveRepository, times(1)).findAll();
    }

    @Dado("O NOME {string} da jogada")
    public void o_nome_da_jogada(String move) {
        System.out.println(move);
        moveInput = new Move();
        moveInput.setMove(move);
    }

    @Quando("Realiza uma busca da jogada pelo NOME")
    public void realiza_busca_jogada_move() throws DataNotFoundException {
        final String moveName = moveInput.getMove();
        when(moveRepository.findByMove(moveName)).thenReturn(Optional.of(moveInput));
        moveResponse = moveService.findByMove(moveName);
    }

    @Entao("A jogada eh exibida")
    public void entao_jogada_exibida() {
        assertEquals(moveInput, moveResponse, "Movimento cadastrado deve ser retornado");
    }

    @Dado("O NOME errado {string} da jogada")
    public void o_nome_errado_da_jogada(String move) {
        System.out.println(move);
        moveInput = new Move();
        moveInput.setMove(move);
    }

    @Quando("Realiza uma busca da jogada pelo NOME errado")
    public void realiza_busca_jogada_nome_errado() {
        final String moveName = moveInput.getMove();
        when(moveRepository.findByMove(moveName)).thenReturn(Optional.empty());
        exception = assertThrows(DataNotFoundException.class,() -> moveService.findByMove(moveName), "se a jpgada não estiver cadastrada, o método deve lançar uma exceção");
    }

    @Entao("Um erro eh exibido")
    public void entao_um_erro_eh_exibido() {
        final String moveName = moveInput.getMove();
        assertEquals("A jogada não está cadastrada!", exception.getMessage(),"a mensagem para a exceção deve ser.... A jogada não está cadastrada!");
        verify(moveRepository, times(1)).findByMove(moveName);
    }

}