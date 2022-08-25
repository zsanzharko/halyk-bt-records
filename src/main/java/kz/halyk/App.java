package kz.halyk;

import kz.halyk.model.Record;
import kz.halyk.utils.CSVReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Main App that to run application
 */
@Slf4j
public class App {
    public static void main(String[] args) throws IOException, ParseException {
        if (args.length == 0)
            throw new RuntimeException("Need add filepath to CLI");

        final String path = args[0];

        List<Record> recordList = CSVReader.read(path);
        log.info(String.format("Total size %d", recordList.size()));
    }
}
