package dev.qilletni.impl.lang.types.list;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.lang.types.DoubleType;
import dev.qilletni.api.lang.types.IntType;
import dev.qilletni.api.music.MusicPopulator;
import dev.qilletni.api.music.supplier.DynamicProvider;
import dev.qilletni.impl.lang.types.AlbumTypeImpl;
import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.impl.lang.types.CollectionTypeImpl;
import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.impl.lang.types.DoubleTypeImpl;
import dev.qilletni.impl.lang.types.SongTypeImpl;
import dev.qilletni.api.lang.types.StringType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Creates instances of {@link ListTypeTransformer}.
 */
public class ListTypeTransformerFactory {

    private final DynamicProvider dynamicProvider;
    private final MusicPopulator musicPopulator;

    public ListTypeTransformerFactory(DynamicProvider dynamicProvider, MusicPopulator musicPopulator) {
        this.dynamicProvider = dynamicProvider;
        this.musicPopulator = musicPopulator;
    }

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
        var songType = new SongTypeImpl(dynamicProvider, stringType.stringValue());
        musicPopulator.initiallyPopulateSong(songType);
        return songType;
    }

    private AlbumType transformStringToAlbum(StringType stringType) {
        var albumType = new AlbumTypeImpl(dynamicProvider, stringType.stringValue());
        musicPopulator.initiallyPopulateAlbum(albumType);
        return albumType;
    }

    private CollectionType transformStringToCollection(StringType stringType) {
        var collectionType = new CollectionTypeImpl(dynamicProvider, stringType.stringValue());
        musicPopulator.initiallyPopulateCollection(collectionType);
        return collectionType;
    }

    private DoubleType transformIntToDouble(IntType intType) {
        return new DoubleTypeImpl(intType.getValue());
    }

}
