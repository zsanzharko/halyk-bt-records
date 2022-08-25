package kz.halyk.utils;

import kz.halyk.model.Record;
import kz.halyk.utils.enums.BTColumns;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class CSVReader {
    private final static Marker mainMarker = MarkerFactory.getMarker("CSV Reader");
    private final static String datePattern = "dd-MMM-yyyy";

    public static List<Record> read(String filePath) throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

        log.info(mainMarker, String.format("Starting read csv file in path: %s", filePath));
        log.info("Starting measuring time");

        final long startTime = System.currentTimeMillis();

        Reader in = new FileReader(refactorFilePath(filePath));
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        List<Record> recordList = new ArrayList<>();

        for (CSVRecord record : records) {
            if (record.get(BTColumns.DATE.getIndex()).equals(BTColumns.DATE.getName())) continue;

            recordList.add(new Record(
                    dateFormat.parse(record.get(BTColumns.DATE.getIndex())),
                    record.get(BTColumns.DESCRIPTION.getIndex()),
                    new BigDecimal(cleanAmount(record.get(BTColumns.DEPOSIT.getIndex()))),
                    new BigDecimal(cleanAmount(record.get(BTColumns.WITHDRAWALS.getIndex()))),
                    new BigDecimal(cleanAmount(record.get(BTColumns.DEPOSIT.getIndex())))));
        }
        long endTime = System.currentTimeMillis();
        log.info("Finished reading data in csv");
        log.info(String.format("Total time parsing data: %s ms", (endTime - startTime)));

        return recordList;
    }

    private static String refactorFilePath(String filePath) {
        return filePath.replaceAll("/", "\\");
    }

    private static String cleanAmount(String amount) {
        return amount.replaceAll(",", "");
    }
}
