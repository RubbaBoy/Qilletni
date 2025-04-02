package dev.qilletni.lib.demo.music.entities;

import dev.qilletni.api.music.User;

public class DemoUser implements User {
    
    private final String id;
    private final String name;

    public DemoUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DemoUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
