import sevenzip.SevenZCodec;
import service.ObjectCodec;

module sevenzip {
    requires plugin;
    requires commons.compress;
    requires xz;

    provides ObjectCodec with SevenZCodec;
}