module is.yarr.qilletni.Qilletni.main {
    uses is.yarr.qilletni.api.auth.ServiceProvider;
    
    requires Qilletni.qilletni.api.main;
    requires Qilletni.qilletni.spotify.main;
    requires org.antlr.antlr4.runtime;
    requires org.slf4j;
    
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.slf4j.impl;
}
