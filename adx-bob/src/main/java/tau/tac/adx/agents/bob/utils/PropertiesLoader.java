package tau.tac.adx.agents.bob.utils;

import java.io.*;
import java.util.Properties;

public class PropertiesLoader {

    public Properties getPropertiesFromFile(String path) throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(new File(path));
        prop.load(input);
        input.close();
        return prop;
    }

    public void setPropertiesToFile(String path, Properties properties) throws IOException {
        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        properties.store(fileOutputStream, "");
        fileOutputStream.close();
    }
}
