package dev.qilletni.api.music;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.SongType;

import java.util.Optional;

/**
 * An interface to check what {@link QilletniType} a String is.
 */
public interface StringIdentifier {

    /**
     * Turns the given String into a meaningful type, either a {@link SongType},
     * {@link CollectionType}, or {@link AlbumType}.
     * 
     * @param string The string to parse, either a URL or an ID.
     * @return The type of string this is
     */
    Optional<QilletniType> parseString(String string);
    
}
