import dev.qilletni.lib.spotify.music.provider.SpotifyServiceProvider;

module qilletni.lib.spotify.main {
    exports dev.qilletni.lib.spotify;
    requires qilletni.api;
    
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

    opens dev.qilletni.lib.spotify.music.auth.pkce to com.google.gson;
    opens dev.qilletni.lib.spotify.music.entities to org.hibernate.orm.core;
    exports dev.qilletni.lib.spotify.music.play;

    provides dev.qilletni.api.auth.ServiceProvider
            with SpotifyServiceProvider;
}
