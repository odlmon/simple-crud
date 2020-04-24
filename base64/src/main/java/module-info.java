import base64.Base64Codec;
import service.ObjectCodec;

module base64 {
    requires plugin;

    provides ObjectCodec with Base64Codec;
}