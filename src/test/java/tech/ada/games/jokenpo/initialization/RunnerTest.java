package tech.ada.games.jokenpo.initialization;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import tech.ada.games.jokenpo.repository.RoleRepository;

public class RunnerTest {

	private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

	@Test
	public void testRun() {

		Runner runner = new Runner(roleRepository);
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

		runner.run((String[]) null);

		verify(roleRepository, times(1)).save(any());

	}
}
