package dev.qilletni.lib.spotify;

import dev.qilletni.lib.spotify.music.QueuePlayActor;

public class BlockingQueueFunctions {

    public void enableBlockingQueue(boolean fastPolling) {
        QueuePlayActor.setBlocking(true);
        QueuePlayActor.setFastPolling(fastPolling);
    }

    public void disableBlockingQueue() {
        QueuePlayActor.setBlocking(false);
    }
    
}
