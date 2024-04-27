package is.yarr.qilletni.music;

import is.yarr.qilletni.api.music.User;

public class DummyUser implements User {
    
    private final String identifier;

    public DummyUser() {
        this.identifier = "dummy-%d".formatted(hashCode());;
    }

    @Override
    public String getId() {
        return identifier;
    }

    @Override
    public String getName() {
        return identifier;
    }
}
