import is.yarr.qilletni.lib.core.StandardLibrary;

module Qilletni.qilletni.lib.core.main {
    exports is.yarr.qilletni.lib.core;
    requires Qilletni.qilletni.api.main;
    
    provides is.yarr.qilletni.api.lib.Library
            with StandardLibrary;
}