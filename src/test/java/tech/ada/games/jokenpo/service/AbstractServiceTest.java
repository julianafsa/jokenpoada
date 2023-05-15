package tech.ada.games.jokenpo.service;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.GameMoveDto;
import tech.ada.games.jokenpo.model.*;

import java.time.LocalDateTime;
import java.util.*;
@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractServiceTest{

    protected GameDto buildGameDto() {
        final GameDto gameDto = new GameDto();
        List<Long> players = Arrays.asList(1L, 2L);
        gameDto.setPlayers(players);
        return gameDto;
    }

    protected GameDto buildGameDto(List<Long> players) {
        final GameDto gameDto = new GameDto();
        gameDto.setPlayers(players);
        return gameDto;
    }

    protected Player buildPlayer(final Long id, final String username, final String name) {
        final Player player = new Player();
        player.setId(id);
        player.setUsername(username);
        player.setPassword("1234");
        player.setName(name);
        final Role role = this.buildRole();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        player.setRoles(roles);
        return player;
    }

    protected Role buildRole() {
        final Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        return role;
    }

    protected Game buildGame(final Player player) {
        final Game game = new Game();
        game.setId(player.getId());
        game.setCreator(player);
        game.setFinished(Boolean.FALSE);
        game.setCreatedAt(LocalDateTime.now());
        return game;
    }

    protected Game buildGame(final Player player, Boolean finished) {
        final Game game = new Game();
        game.setId(player.getId());
        game.setCreator(player);
        game.setFinished(finished);
        game.setCreatedAt(LocalDateTime.now());
        return game;
    }

    protected GameMoveDto buildGameMoveDTO() {
        GameMoveDto gmDto = new GameMoveDto();

        gmDto.setMoveId(1L);
        gmDto.setGameId(1L);
        return gmDto;
    }
    protected Move buildMove(final Long id, final String moveName) {
        Move move = new Move();
        move.setMove(moveName);
        move.setId(id);
        return move;
    }

    protected PlayerMove buildPlayerMove(Long id, Game game, Player player) {
        PlayerMove pMove = new PlayerMove();
        pMove.setGame(game);
        pMove.setPlayer(player);
        pMove.setId(id);
        return pMove;
    }
    protected PlayerMove buildPlayerMove(Long id, Game game, Player player, Move move) {
        PlayerMove pMove = this.buildPlayerMove(id, game, player);
        pMove.setMove(move);
        return pMove;
    }

    protected List<Tuple> buildTupleList(int n) {
        final List<Tuple> list = new ArrayList<>();
        for (int i = 1, j = n; i <= n; i++, j--) {
            list.add(this.buildTuple(i, (long) j));
        }
        return list;
    }

    private Tuple buildTuple(int index, Long victories) {
        return new Tuple() {
            @Override
            public <X> X get(TupleElement<X> tupleElement) {
                return null;
            }

            @Override
            public <X> X get(String alias, Class<X> type) {
                return null;
            }

            @Override
            public Object get(String alias) {
                return null;
            }

            @Override
            public <X> X get(int i, Class<X> type) {
                return null;
            }

            @Override
            public Object get(int i) {
                Map<Integer, Object> tuple = new HashMap<>();
                // Populate tuple object
                tuple.put(0, (long)(index));
                tuple.put(1, "player" + index);
                tuple.put(2, "Player " + index);
                tuple.put(3, victories);
                return tuple.get(i);
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public List<TupleElement<?>> getElements() {
                return null;
            }
        };
    }

}