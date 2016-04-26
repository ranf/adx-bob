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


    public void serialize(Object obj, String filePath) throws IOException {
        File file = new File(filePath);
        Writer writer = new FileWriter(file);
        gson.toJson(obj, writer);
        writer.close();
    }

    public <T> T deserialize(String filePath, Type type) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        Reader reader = new FileReader(file);
        T result = gson.fromJson(reader, type);
        reader.close();
        return result;
    }
}
