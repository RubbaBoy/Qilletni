import dev.qilletni.lib.demo.music.provider.DemoServiceProvider;

module qilletni.lib.providerdemo.main {

    requires qilletni.api;

    provides dev.qilletni.api.auth.ServiceProvider
            with DemoServiceProvider;
}