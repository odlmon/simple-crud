package sample.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonSerializer implements Serializer {

    private Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @Override
    public void serialize(Object[] objects, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(Arrays.stream(objects)
                    .map(object -> gson.toJson(new JsonWrapper(object.getClass().getName(), gson.toJson(object))))
                    .collect(Collectors.joining("\n---\n")));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object[] deserialize(File file) {
        try (FileReader reader = new FileReader(file)) {
            String[] strings = new BufferedReader(reader)
                    .lines()
                    .collect(Collectors.joining(""))
                    .split("---");
            Object[] objects = new Object[strings.length];
            for (int i = 0; i < strings.length; i++) {
                JsonWrapper jsonWrapper = gson.fromJson(strings[i], JsonWrapper.class);
                objects[i] = gson.fromJson(jsonWrapper.getValue(), Class.forName(jsonWrapper.getClassName()));
            }
            return objects;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
