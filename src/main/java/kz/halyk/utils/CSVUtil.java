package kz.halyk.utils;

import kz.halyk.App;
import kz.halyk.model.OutputRecord;
import kz.halyk.model.Record;
import kz.halyk.utils.enums.BTColumns;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.DelegatingProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public final class CSVUtil {
    private final static Marker mainMarker = MarkerFactory.getMarker("CSV Reader");

    public static List<Record> read(String filePath) throws IOException, ParseException {

        log.info(mainMarker, String.format("Starting read csv file in path: %s", filePath));
        log.info("Starting measuring time");

        final long startTime = System.currentTimeMillis();

        Reader in = new FileReader(refactorFilePath(filePath));
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        List<Record> recordList = new ArrayList<>();

        try (ProgressBar progressBar = new ProgressBarBuilder()
                .setInitialMax(100)
                .setTaskName("Read Data")
                .setConsumer(new DelegatingProgressBarConsumer(log::info))
                .build()) {
            progressBar.maxHint(-1);

            for (CSVRecord record : records) {
                if (record.get(BTColumns.DATE.getIndex()).equals(BTColumns.DATE.getName())) continue;

                recordList.add(new Record(
                        App.dateFormat.parse(record.get(BTColumns.DATE.getIndex())),
                        record.get(BTColumns.DESCRIPTION.getIndex()),
                        new BigDecimal(cleanAmount(record.get(BTColumns.DEPOSIT.getIndex()))),
                        new BigDecimal(cleanAmount(record.get(BTColumns.WITHDRAWALS.getIndex()))),
                        new BigDecimal(cleanAmount(record.get(BTColumns.DEPOSIT.getIndex())))));
                progressBar.step();
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("Finished reading data in csv");
        log.info(String.format("Total time parsing data: %s ms", (endTime - startTime)));

        return recordList;
    }

    public static void writeRecords(@NonNull final Set<OutputRecord> outputRecords) throws ParseException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(App.outputPath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader(BTColumns.DATE.getName(), "Type", "Min", "Max", "Avg"))) {
            for (OutputRecord o : outputRecords) {
                csvPrinter.printRecord(App.dateFormat.format(o.getDate()), o.getType(), o.getMin(), o.getMax(), o.getAverage());
            }
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
