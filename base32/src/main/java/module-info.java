import base32.Base32Codec;
import service.ObjectCodec;

module base32 {
    requires plugin;
    requires commons.codec;

    provides ObjectCodec with Base32Codec;
}