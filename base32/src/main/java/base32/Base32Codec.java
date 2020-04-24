package base32;

import org.apache.commons.codec.binary.Base32;
import service.ObjectCodec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Base32Codec implements ObjectCodec {

    private final Base32 base32 = new Base32();

    @Override
    public void encode(File file) {
        try {
            byte[] encodedSequence = base32.encode(Files.readAllBytes(file.toPath()));
            Files.write(file.toPath(), encodedSequence);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(File file) {
        try {
            byte[] decodedSequence = base32.decode(Files.readAllBytes(file.toPath()));
            Files.write(file.toPath(), decodedSequence);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
