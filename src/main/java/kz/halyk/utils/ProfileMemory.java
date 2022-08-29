package kz.halyk.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class ProfileMemory implements Runnable {
    public static final String PROFILE_CONSOLE_COLOR = "\u001B[33m";
    private static final LinkedList<Long> memoryStatPerSec = new LinkedList<>();
    private final String nameThread;
    private boolean isActive;

    public ProfileMemory(String nameThread) {
        this.isActive = false;
        this.nameThread = nameThread;
    }

    @Override
    public void run() {
        log.info(String.format("%s started...", Thread.currentThread().getName()));

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (isActive) {
                profileMemory();
            } else {
                executor.shutdown();
                log.info(String.format("%s is disabled...", Thread.currentThread().getName()));
            }
        }, 0, 1, SECONDS);
    }

    public void disable() {
        isActive = false;
    }

    public void enable() {
        if (isActive) return;

        this.isActive = true;
        new Thread(this, nameThread).start();
    }

    public void getStatistic() {
        if (memoryStatPerSec.size() == 0) {
            log.info("Empty information about JVM memory");
            return;
        }
        log.info("Information about JVM Memory");
        generateStatisticInConsole();
    }

    private void generateStatisticInConsole() {
        final int maxValue = memoryStatPerSec.stream().mapToInt(Math::toIntExact).max().orElse(-1) / 10;

        StringBuilder verticalResult = new StringBuilder(PROFILE_CONSOLE_COLOR);

        AtomicInteger counter = new AtomicInteger(1);
        memoryStatPerSec.forEach(memory -> {
            int memoryLengthShowConsole = memory.intValue() / 10;
            verticalResult
                    .append(String.valueOf(memoryLengthShowConsole == 0 ? '<' : '>').repeat(memoryLengthShowConsole == 0 ? 1 : memoryLengthShowConsole))
                    .append(" ").append(memory)
                    .append(" ".repeat((maxValue - memoryLengthShowConsole) + 1))
                    .append(counter.getAndIncrement()).append("sec").append("\n");
        });
        System.out.println(verticalResult);
    }

    private void profileMemory() {
        final Runtime runtime = Runtime.getRuntime();
        memoryStatPerSec.add((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
    }
}
