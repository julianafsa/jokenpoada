package tech.ada.games.jokenpo.acceptance.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import tech.ada.games.jokenpo.dto.MoveDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.repository.MoveRepository;
import tech.ada.games.jokenpo.service.MoveService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoveServiceSteps {

    private final MoveRepository moveRepository;
    private final MoveService moveService;

    private String moveName;
    private MoveDto moveDto;
    private List<Move> moves;
    private Move expectedMove;
    private Move moveResponse;
    private DataNotFoundException dataNotFoundException;
    private DataConflictException dataConflictException;
    private BadRequestException badRequestException;

    public MoveServiceSteps() {
        this.moveRepository = Mockito.mock(MoveRepository.class);
        this.moveService = new MoveService(moveRepository);
    }

    // 1. Scenario Outline: Create moves
    @Given("A {string} move")
    public void a_name(String move) {
        moveDto = new MoveDto(move);
        when(moveRepository.existsByMove(moveDto.getMove())).thenReturn(Boolean.FALSE);
    }

    @When("Create a move")
    public void create_a_move() throws DataConflictException, BadRequestException {
        moveService.createMove(moveDto);
    }
    @Then("Move is created")
    public void move_is_created() {
        verify(moveRepository, times(1)).save(any());
    }

    // 2. Scenario: Create move already created
    @Given("A move {string} already created")
    public void a_move_name_already_created(String move) {
        moveDto = new MoveDto(move);
        when(moveRepository.existsByMove(moveDto.getMove())).thenReturn(Boolean.TRUE);
    }

    @When("Create a move already created")
    public void create_a_move_already_created() {
        dataConflictException = assertThrows(DataConflictException.class,() ->
                moveService.createMove(moveDto), "se existir movimento cadastrado com o mesmo nome, o método deve lançar uma exceção");
    }

    @Then("Move is not created")
    public void move_is_not_created() {
        assertEquals("A jogada já está cadastrada!", dataConflictException.getMessage(),"a mensagem para a exceção deve ser.... A jogada já está cadastrada!");
        verify(moveRepository, times(1)).existsByMove(anyString());
    }

    // 3. Scenario: Create invalid move
    @Given("A {string} name move")
    public void a_move(String move) {
        moveDto = new MoveDto(move);
    }

    @When("Trying to create a invalid move")
    public void trying_to_create_a_invalid_move() {
        badRequestException = assertThrows(BadRequestException.class,() -> moveService.createMove(moveDto), "se o usuário tentar cadastrar uma jogada inválida, o método deve lançar uma exceção");
    }

    @Then("Move is not created and a message error is returned")
    public void move_is_not_created_and_a_message_error_is_returned() {
        assertEquals("Você pode cadastrar apenas os movimentos Spock, Tesoura, Papel, Pedra e Lagarto", badRequestException.getMessage(),"a mensagem para a exceção deve ser.... Você pode cadastrar apenas os movimentos Spock, Tesoura, Papel, Pedra e Lagarto");
        verify(moveRepository, times(1)).existsByMove(anyString());
    }

    // 4. Scenario: Listing all moves
    @Given("A empty list of moves")
    public void a_empty_list_of_moves() {
        moves = new ArrayList<>();
        Mockito.when(moveRepository.findAll()).thenReturn(List.of(new Move()));
    }
    @When("Search all moves")
    public void search_all_moves() throws DataNotFoundException {
        moves = moveService.findMoves();
    }
    @Then("Moves list is displayed")
    public void moves_list_is_displayed() {
        assertFalse(moves.isEmpty(),"A lista não pode estar vazia se existir movimentos cadastrados");
    }

    // 5. Scenario: Displaying error if there are no registered moves
    @When("Search all empty moves")
    public void search_all_empty_moves() {
        when(moveRepository.findAll()).thenReturn(Collections.emptyList());
        dataNotFoundException = assertThrows(DataNotFoundException.class,() -> moveService.findMoves(), "se não existir movimentos cadastrados, o método deve lançar uma exceção");
    }
    @Then("An error message is displayed")
    public void an_error_message_is_displayed() {
        assertEquals("Não há jogadas cadastradas!", dataNotFoundException.getMessage(),"a mensagem para a exceção deve ser.... Não há jogadas cadastradas!");
        verify(moveRepository, times(1)).findAll();
    }

    // 6. Scenario: Searching for a move by name
    @Given("A move named {string}")
    public void a_move_named(String move) {
        expectedMove = new Move(1L, move, null);
        final String moveName = expectedMove.getMove();
        when(moveRepository.findByMove(moveName)).thenReturn(Optional.of(expectedMove));
    }

    @When("Search for a move by name")
    public void search_for_a_move_by_name() throws DataNotFoundException {
        moveResponse = moveService.findByMove(expectedMove.getMove());
    }

    @Then("The move is displayed")
    public void the_move_is_displayed() {
        assertEquals(expectedMove, moveResponse, "Movimento cadastrado deve ser retornado");
    }

    // 7. Searching for an invalid move
    @Given("A {string} move to be searched")
    public void a_move_to_be_searched(String invalidMove) {
        moveName = invalidMove;
        when(moveRepository.findByMove(moveName)).thenReturn(Optional.empty());
    }

    @When("Searching for an invalide move")
    public void searching_for_an_invalide_move() {
        dataNotFoundException = assertThrows(DataNotFoundException.class,() -> moveService.findByMove(moveName), "se a jpgada não estiver cadastrada, o método deve lançar uma exceção");
    }

    @Then("Move is not returned and an error message is displayed")
    public void move_is_not_returned_and_an_error_message_is_displayed() {
        assertEquals("A jogada não está cadastrada!", dataNotFoundException.getMessage(),"a mensagem para a exceção deve ser.... A jogada não está cadastrada!");
        verify(moveRepository, times(1)).findByMove(moveName);
    }

}