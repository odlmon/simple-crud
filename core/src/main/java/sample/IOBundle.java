package sample;

import sample.serialize.Serializer;
import service.ObjectCodec;

import java.io.File;
import java.util.Optional;

public record IOBundle(Serializer serializer, Optional<ObjectCodec> objectCodec, File file) {
}
