package tau.tac.adx.agents.bob.plumbing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	
	public void setPropertiesToResource(String path,Properties properties) throws IOException{
		String output = getClass().getClassLoader().getResource(path).getPath();
		if (output != null) {
			File file = new File(output);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			properties.store(fileOutputStream,"");
			fileOutputStream.close();
		} else {
			throw new FileNotFoundException("could not save config at "
					+ path);
		}
	}
}
