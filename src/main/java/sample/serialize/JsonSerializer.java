package sample.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class JsonSerializer implements Serializer {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void serialize(Object object, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(new JsonWrapper(object.getClass().getName(), gson.toJson(object)), writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object deserialize(File file) {
        try {
            JsonWrapper jsonWrapper = gson.fromJson(new FileReader(file), JsonWrapper.class);
            return gson.fromJson(jsonWrapper.getValue(), Class.forName(jsonWrapper.getClassName()));
        } catch (FileNotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
