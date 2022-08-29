package kz.halyk;

import kz.halyk.service.ComputingService;
import kz.halyk.utils.ProfileMemory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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

        ProfileMemory profileMemory = new ProfileMemory("Profile Memory");

        new ComputingService().fastCompute(path);

        profileMemory.disable();

    }
}
