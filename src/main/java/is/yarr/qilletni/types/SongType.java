package is.yarr.qilletni.types;

public final class SongType implements QilletniType {
    @Override
    public String stringValue() {
        return "~song~";
    }

    @Override
    public String typeName() {
        return "song";
    }
}
