package tau.tac.adx.agents.bob.utils;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.io.*;
import java.lang.reflect.Type;

public class FileSerializer {

    private Gson gson;

    @Inject
    public FileSerializer(Gson gson) {
        this.gson = gson;
    }


    public void serialize(Object obj, String resourceName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        Writer writer = new FileWriter(file);
        gson.toJson(obj, writer);
        writer.close();
    }

    public <T> T deserialize(String resourceName, Type type) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        Reader reader = new FileReader(file);
        T result = gson.fromJson(reader, type);
        reader.close();
        return result;
    }
}
