package kz.halyk.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

@Slf4j
public class ProfileMemory implements Runnable {
    private boolean isActive;

    public ProfileMemory(String nameThread) {
        this.isActive = true;
        new Thread(this, nameThread).start();
    }

    @Override
    public void run() {
        log.info(String.format("%s started... \n", Thread.currentThread().getName()));

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (isActive) {
                profileMemory();
            } else executor.shutdown();
        }, 1, 2, SECONDS);
    }

    public void disable() {
        isActive = false;
    }

    private void profileMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 100);
//        log.info(String.format("Used memory JVM: %s MB\r", usedMem));
        System.out.printf("Used memory JVM: %s MB\t\r", usedMem);
    }
}
