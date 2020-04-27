package base64;

import service.ExtensionData;
import service.ObjectCodec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class Base64Codec implements ObjectCodec {

    public void encode(File file) {
        try {
            byte[] encodedSequence = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            Files.write(file.toPath(), encodedSequence);
            file.renameTo(new File(file.getCanonicalPath() + ".b64"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decode(File file) {
        try {
            byte[] decodedSequence = Base64.getDecoder().decode(Files.readAllBytes(file.toPath()));
            Files.write(file.toPath(), decodedSequence);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExtensionData getExtensionData() {
        return new ExtensionData("Base64 files (*.b64)", "*.b64");
    }
}
