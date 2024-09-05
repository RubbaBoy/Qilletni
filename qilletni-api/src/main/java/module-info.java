module Qilletni.qilletni.api.main {
    requires org.jetbrains.annotations;
    exports is.yarr.qilletni.api;
    exports is.yarr.qilletni.api.auth;
    exports is.yarr.qilletni.api.music;
    exports is.yarr.qilletni.api.music.factories;
    
    exports is.yarr.qilletni.api.lang.stack;
    exports is.yarr.qilletni.api.lang.table;
    exports is.yarr.qilletni.api.lang.types;
    exports is.yarr.qilletni.api.lang.types.conversion;
    exports is.yarr.qilletni.api.lang.types.typeclass;
    exports is.yarr.qilletni.api.lang.types.album;
    exports is.yarr.qilletni.api.lang.types.entity;
    exports is.yarr.qilletni.api.lang.types.collection;
    exports is.yarr.qilletni.api.lang.types.song;
    exports is.yarr.qilletni.api.lang.types.list;
    exports is.yarr.qilletni.api.lang.types.weights;
    exports is.yarr.qilletni.api.lang.docs;
    exports is.yarr.qilletni.api.lang.docs.structure;
    exports is.yarr.qilletni.api.lang.docs.structure.item;
    exports is.yarr.qilletni.api.lang.docs.structure.text;
    exports is.yarr.qilletni.api.lang.docs.structure.text.inner;
    exports is.yarr.qilletni.api.lib;
    exports is.yarr.qilletni.api.lib.annotations;
    exports is.yarr.qilletni.api.lib.qll;
    exports is.yarr.qilletni.api.lang.internal;
    exports is.yarr.qilletni.api.music.supplier;
    exports is.yarr.qilletni.api.exceptions;
    exports is.yarr.qilletni.api.music.orchestration;
}
