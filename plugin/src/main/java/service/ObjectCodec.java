package service;

import java.io.File;

public interface ObjectCodec {

    void encode(File file);

    void decode(File file);

    ExtensionData getExtensionData();
}
