package kz.halyk.profiler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProfileTimer {
    private final Marker marker = MarkerFactory.getMarker("Profile Timer");
    private Integer sizeTime = 0;
    private final List<Long> startTimes = new ArrayList<>();
    private final List<Long> endTimes = new ArrayList<>();
    private final String name;
    private boolean isActive;

    public ProfileTimer(String name) {
        this.name = name;
        this.isActive = false;
    }

    public void start() {
        if (isActive) {
            log.info(marker, "Profile time is active");
            return;
        }
        startTimes.add(System.currentTimeMillis());
        isActive = true;
        log.info(marker, String.format("%s started...", name));
    }

    public void stop() {
        if (!isActive) {
            log.info(marker, "Profile time is not active");
            return;
        }
        endTimes.add(System.currentTimeMillis());
        sizeTime++;
        isActive = false;
        log.info(String.format("%s stopped...", name));
    }

    public void getResults() {
        if (isActive) {
            log.info(marker, "Profile time is active");
            return;
        }

        log.info(marker, String.format("Profile Time (%s) Results", name));
        for (int i = 0; i < sizeTime; i++) {
            long total = endTimes.get(i) - startTimes.get(i);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(total);
            long seconds = (TimeUnit.MILLISECONDS.toSeconds(total) % 60);
            log.info(marker, String.format("%d. %d min %d sec", (i), minutes, seconds));
        }
    }
}
