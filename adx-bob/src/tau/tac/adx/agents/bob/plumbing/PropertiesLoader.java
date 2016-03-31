package tau.tac.adx.agents.bob.plumbing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

	public Properties getPropertiesFromResource(String path) throws IOException{
		Properties prop = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream(
				path);
		if (input != null) {
			prop.load(input);
		} else {
			throw new FileNotFoundException("could not load config at "
					+ path);
		}
		return prop;
	}
}
