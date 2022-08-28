package kz.halyk;

import kz.halyk.service.ComputingService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Main App that to run application
 */
@Slf4j
public class App {

    public final static String datePattern = "dd-MMM-yyyy";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(App.datePattern);

    public static String outputPath = "./output.csv";

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length == 0)
            throw new RuntimeException("Need add filepath to CLI. Example: \"local\\path.csv\"");

        final String path = args[0];

        profileMemory();

        ComputingService.fastCompute(path);

        profileMemory();
    }

    private static void profileMemory() {
        Runtime runtime = Runtime.getRuntime();
        final long usedMem = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Used memory JVM: %s MB\n", usedMem / 1024);
    }
}
