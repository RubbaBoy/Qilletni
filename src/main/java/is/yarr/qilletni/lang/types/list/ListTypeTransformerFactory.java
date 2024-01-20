package is.yarr.qilletni.lang.types.list;

import is.yarr.qilletni.lang.types.AlbumType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

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
        
        return listGenerator;
    }
    
    private SongType transformStringToSong(StringType stringType) {
        return new SongType(stringType.stringValue());
    }
    
    private AlbumType transformStringToAlbum(StringType stringType) {
        return new AlbumType(stringType.stringValue());
    }
    
    private CollectionType transformStringToCollection(StringType stringType) {
        return new CollectionType(stringType.stringValue());
    }
    
}
