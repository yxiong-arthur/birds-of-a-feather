package com.swift.birdsofafeather;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakeMessageListener extends com.google.android.gms.nearby.messages.MessageListener {
    private final com.google.android.gms.nearby.messages.MessageListener messageListener;
    private final ScheduledExecutorService executor;

    public FakeMessageListener(com.google.android.gms.nearby.messages.MessageListener realMessageListener, int frequency, String messageStr) {
        this.messageListener = realMessageListener;
        this.executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            Message message = new Message(messageStr.getBytes(StandardCharsets.UTF_8));
            this.messageListener.onFound(message);
            this.messageListener.onLost(message);
        }, 0, frequency, TimeUnit.SECONDS);
    }

}