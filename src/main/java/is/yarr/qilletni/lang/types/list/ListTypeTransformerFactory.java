package is.yarr.qilletni.lang.types.list;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.DoubleType;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.lang.types.AlbumTypeImpl;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.CollectionTypeImpl;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.lang.types.DoubleTypeImpl;
import is.yarr.qilletni.lang.types.IntTypeImpl;
import is.yarr.qilletni.lang.types.SongTypeImpl;
import is.yarr.qilletni.api.lang.types.StringType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Creates instances of {@link ListTypeTransformer}.
 */
public class ListTypeTransformerFactory {

    /**
     * Creates the default {@link ListTypeTransformer}.
     * 
     * @return The created {@link ListTypeTransformer}
     */
    public ListTypeTransformer createListGenerator() {
        var listGenerator = new ListTypeTransformer();
        
        listGenerator.registerListTransformer(QilletniTypeClass.STRING, QilletniTypeClass.SONG, this::transformStringToSong);
        listGenerator.registerListTransformer(QilletniTypeClass.STRING, QilletniTypeClass.ALBUM, this::transformStringToAlbum);
        listGenerator.registerListTransformer(QilletniTypeClass.STRING, QilletniTypeClass.COLLECTION, this::transformStringToCollection);
        
        listGenerator.registerListTransformer(QilletniTypeClass.INT, QilletniTypeClass.DOUBLE, this::transformIntToDouble);
        
        return listGenerator;
    }

    private SongType transformStringToSong(StringType stringType) {
        return new SongTypeImpl(stringType.stringValue());
    }

    private AlbumType transformStringToAlbum(StringType stringType) {
        return new AlbumTypeImpl(stringType.stringValue());
    }

    private CollectionType transformStringToCollection(StringType stringType) {
        return new CollectionTypeImpl(stringType.stringValue());
    }

    private DoubleType transformIntToDouble(IntType intType) {
        return new DoubleTypeImpl(intType.getValue());
    }

}
