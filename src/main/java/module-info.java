module qilletni.impl {

    exports is.yarr.qilletni;
    exports is.yarr.qilletni.lib;
    exports is.yarr.qilletni.lib.persistence;
    exports is.yarr.qilletni.lang.docs;
    exports is.yarr.qilletni.lang.runner;

    requires qilletni.api;
    requires org.antlr.antlr4.runtime;
    requires org.slf4j;
    
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.slf4j.impl;
    requires com.google.gson;
    requires java.desktop;
}
