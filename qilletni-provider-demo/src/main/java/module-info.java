module Qilletni.qilletni.provider.demo.main {

    requires Qilletni.qilletni.api.main;

    provides is.yarr.qilletni.api.auth.ServiceProvider
            with is.yarr.qilletni.music.demo.provider.DemoServiceProvider;
}