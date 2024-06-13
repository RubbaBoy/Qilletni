module is.yarr.qilletni.Qilletni.main {
    uses is.yarr.qilletni.api.auth.ServiceProvider;
    uses is.yarr.qilletni.api.lib.Library;

    exports is.yarr.qilletni;
    exports is.yarr.qilletni.lib;
    exports is.yarr.qilletni.lang.runner;
    
    requires Qilletni.qilletni.api.main;
//    requires Qilletni.qilletni.spotify.main;
    requires org.antlr.antlr4.runtime;
    requires org.slf4j;
    
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.slf4j.impl;
    requires com.google.gson;
}
