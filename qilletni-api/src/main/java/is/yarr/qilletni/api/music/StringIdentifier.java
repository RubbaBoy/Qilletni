package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.lang.types.QilletniType;

import java.util.Optional;

/**
 * An interface to check what {@link is.yarr.qilletni.api.lang.types.QilletniType} a String is.
 */
public interface StringIdentifier {

    /**
     * Turns the given String into a meaningful type, either a {@link is.yarr.qilletni.api.lang.types.SongType},
     * {@link is.yarr.qilletni.api.lang.types.CollectionType}, or {@link is.yarr.qilletni.api.lang.types.AlbumType}.
     * 
     * @param string The string to parse, either a URL or an ID.
     * @return The type of string this is
     */
    Optional<QilletniType> parseString(String string);
    
}
