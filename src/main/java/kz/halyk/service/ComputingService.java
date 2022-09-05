package kz.halyk.service;

import kz.halyk.App;
import kz.halyk.model.OutputRecord;
import kz.halyk.model.Record;
import kz.halyk.utils.ProfileTimer;
import kz.halyk.utils.enums.BTColumns;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static kz.halyk.utils.CSVUtil.cleanAmount;
import static kz.halyk.utils.CSVUtil.refactorFilePath;

@Slf4j
public final class ComputingService {
    ProfileTimer timer = new ProfileTimer("Computer Service");

    public void fastCompute(String filePath) throws IOException, ParseException {
        log.info("Data processing started...");
        timer.start();

        DocumentService documentService = new DocumentService(App.outputPath);
        Reader in = new FileReader(refactorFilePath(filePath));
        Iterator<CSVRecord> records = CSVFormat.EXCEL.parse(in).iterator();

        final List<Record> dayRecords = new ArrayList<>();
        Date currentDate = null;

        while (records.hasNext()) {
            CSVRecord csvRecord = records.next();
            if (csvRecord.get(BTColumns.DATE.getIndex()).equals(BTColumns.DATE.getName())) continue;
            if (Double.parseDouble(cleanAmount(csvRecord.get(BTColumns.WITHDRAWALS.getIndex()))) == 0.0) continue;

            Record record = new Record(
                    App.dateFormat.parse(csvRecord.get(BTColumns.DATE.getIndex())),
                    csvRecord.get(BTColumns.DESCRIPTION.getIndex()),
                    new BigDecimal(cleanAmount(csvRecord.get(BTColumns.WITHDRAWALS.getIndex()))));

            if (currentDate != null && currentDate.equals(record.getDate())) {
                dayRecords.add(record);
            } else {
                if (currentDate != null || !dayRecords.isEmpty()) {
                    documentService.write(fastFindWithdrawlsData(dayRecords));
                    documentService.write(fastFindWithdrawlsByDescription(dayRecords));
                }
                currentDate = record.getDate();
                dayRecords.clear();
                dayRecords.add(record);
            }
        }

        if (currentDate != null || !dayRecords.isEmpty()) {
            documentService.write(fastFindWithdrawlsData(dayRecords));
            documentService.write(fastFindWithdrawlsByDescription(dayRecords));
            dayRecords.clear();
        }
        log.info("Data processing is finished...");
        timer.stop();
        timer.getResults();
    }

    private OutputRecord fastFindWithdrawlsData(@NonNull final List<Record> dayRecords) {
        if (dayRecords.isEmpty()) return null;
        return new OutputRecord(dayRecords.get(0).getDate(), "",
                findMin(dayRecords),
                findMax(dayRecords),
                findAvg(dayRecords));
    }

    private Set<OutputRecord> fastFindWithdrawlsByDescription(final List<Record> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return new HashSet<>();
        Set<OutputRecord> outputRecords = new HashSet<>();
        dayRecords.forEach(record -> {
            List<Record> records = dayRecords.stream()
                    .filter(r -> r.getDescription().equals(record.getDescription()))
                    .toList();

            outputRecords.add(new OutputRecord(
                    records.get(0).getDate(),
                    records.get(0).getDescription(),
                    findMin(records),
                    findMax(records),
                    findAvg(records)));
        });
        return outputRecords;
    }

    private static BigDecimal findMin(List<Record> records) {
        return records.stream()
                .map(Record::getWithdrawal)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal findAvg(List<Record> records) {
        return BigDecimal.valueOf(records.stream()
                .map(Record::getWithdrawal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue() / records.size());
    }

    private static BigDecimal findMax(List<Record> records) {
        return records.stream()
                .map(Record::getWithdrawal)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }
}
