import zip.ZipCodec;
import service.ObjectCodec;

module zip {
    requires plugin;

    provides ObjectCodec with ZipCodec;
}