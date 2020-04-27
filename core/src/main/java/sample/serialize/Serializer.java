package sample.serialize;

import java.io.File;

public interface Serializer {

    void serialize(Object[] objects, File file);

    Object[] deserialize(File file);
}
