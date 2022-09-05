package kz.halyk.utils;

import kz.halyk.App;
import kz.halyk.model.OutputRecord;
import kz.halyk.utils.enums.BTColumns;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

@Slf4j
public final class CSVUtil {
    private final static Marker mainMarker = MarkerFactory.getMarker("CSV Reader");

    public static void writeRecords(@NonNull final List<OutputRecord> outputRecords) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(App.outputPath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader(BTColumns.DATE.getName(), "Type", "Min", "Max", "Avg"))) {
            log.info(mainMarker, String.format("Starting write date to path: %s", App.outputPath));
            for (OutputRecord o : outputRecords) {
                csvPrinter.printRecord(App.dateFormat.format(o.getDate()), o.getType(), o.getMin(), o.getMax(), o.getAverage());
            }
            log.info(mainMarker, String.format("Finished writing. Total size %s", outputRecords.size()));
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String refactorFilePath(String filePath) {
        return filePath.replaceAll("/", "\\");
    }

    public static String cleanAmount(String amount) {
        return amount.replaceAll(",", "");
    }
}
