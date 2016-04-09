package tau.tac.adax.agents.bob;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class BaseTestCase {

	@Before
	public void initMocks(){
		MockitoAnnotations.initMocks(this);
	}
}
