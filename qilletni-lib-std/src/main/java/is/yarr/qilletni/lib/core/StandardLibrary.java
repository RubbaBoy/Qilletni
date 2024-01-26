package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lib.Library;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class StandardLibrary implements Library {
    
    @Override
    public String getName() {
        return "Standard Library";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public List<String> autoImportFiles() {
        return List.of("core.ql");
    }

    @Override
    public List<Class<?>> getNativeClasses() {
        return List.of(AlbumFunctions.class, ArtistFunctions.class, CollectionFunctions.class, CoreFunctions.class,
                ListFunctions.class, MapFunctions.class, MathFunctions.class, SongFunctions.class, StringFunctions.class);
    }

    @Override
    public String getImportName() {
        return "std";
    }

    @Override
    public Optional<InputStream> readPath(String path) {
        return Optional.ofNullable(getClass().getResourceAsStream("/" + path));
    }
}
