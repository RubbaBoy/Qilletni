module Qilletni.qilletni.spotify.main {
//    exports is.yarr.qilletni.music.spotify;
    exports is.yarr.qilletni.lib.spotify;
    requires Qilletni.qilletni.api.main;
    
    requires java.persistence;
    requires se.michaelthelin.spotify;

    requires org.slf4j;
    requires com.google.gson;
    requires org.eclipse.jetty.server;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires java.desktop;
    requires java.sql;
    requires jdk.jshell;

    requires net.bytebuddy;
    requires org.hibernate.orm.core;
    requires jdk.unsupported;
    requires java.xml.bind;
    requires java.naming;
    requires com.sun.xml.bind;
    requires com.fasterxml.classmate;

    opens is.yarr.qilletni.music.spotify.auth.pkce to com.google.gson;
    opens is.yarr.qilletni.music.spotify.entities to org.hibernate.orm.core;
    exports is.yarr.qilletni.music.spotify.play;

    provides is.yarr.qilletni.api.auth.ServiceProvider
            with is.yarr.qilletni.music.spotify.provider.SpotifyServiceProvider;

    provides is.yarr.qilletni.api.lib.Library
            with is.yarr.qilletni.lib.spotify.SpotifyLibrary;
}
