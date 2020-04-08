package sample.serialize;

import java.io.*;
import java.util.ArrayList;

public class BinarySerializer implements Serializer {

    @Override
    public void serialize(Object[] objects, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Object object : objects) {
                oos.writeObject(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object[] deserialize(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Object> result = new ArrayList<>();
            Object object;
            while ((object = ois.readObject()) != null) {
                result.add(object);
            }
            return result.toArray();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
