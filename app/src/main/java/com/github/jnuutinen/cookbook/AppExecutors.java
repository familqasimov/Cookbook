package com.github.jnuutinen.cookbook;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final Executor diskIo;
    private final Executor mainThread;

    private AppExecutors(Executor diskIo, Executor mainThread) {
        this.diskIo = diskIo;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
    }

    public Executor diskIo() {
        return diskIo;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
