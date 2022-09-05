package kz.halyk.service;

import kz.halyk.App;
import kz.halyk.model.OutputRecord;
import kz.halyk.model.Record;
import kz.halyk.utils.ProfileTimer;
import kz.halyk.utils.enums.BTColumns;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static kz.halyk.utils.CSVUtil.cleanAmount;

@Slf4j
public final class ComputingService {
    private final ProfileTimer timer = new ProfileTimer("Computer Service");
    private final DocumentService documentService; // create service that working with getting and saving data to csv

    public ComputingService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public ComputingService() {
        this.documentService = new DocumentService(App.outputPath);
    }

    public void fastCompute(String filePath) throws IOException, ParseException {
        log.info("Data processing started...");
        timer.start();

        Iterator<CSVRecord> records = documentService.getData(filePath).iterator();

        final List<Record> dayRecords = new LinkedList<>(); // splitting into day
        Date currentDate = null; // checking date to manipulate

        while (records.hasNext()) {
            CSVRecord csvRecord = records.next();
            if (isCorrectRecord(csvRecord)) continue; // checking record is correct

            Record record = new Record(
                    App.dateFormat.parse(csvRecord.get(BTColumns.DATE.getIndex())),
                    csvRecord.get(BTColumns.DESCRIPTION.getIndex()),
                    new BigDecimal(cleanAmount(csvRecord.get(BTColumns.WITHDRAWALS.getIndex()))));

            if (currentDate != null && currentDate.equals(record.getDate())) {
                dayRecords.add(record);
            } else {
                if (currentDate != null || !dayRecords.isEmpty()) {
                    documentService.write(findWithdrawlsPerDay(dayRecords, currentDate));
                    documentService.write(findWithdrawlsByDescription(dayRecords));
                }
                currentDate = record.getDate();
                dayRecords.clear();
                dayRecords.add(record);
            }
        }
        log.info("Data processing is finished...");
        timer.stop();
        timer.getResults();
    }

    private boolean isCorrectRecord(CSVRecord csvRecord) {
        return  (Double.parseDouble(cleanAmount(csvRecord.get(BTColumns.WITHDRAWALS.getIndex()))) == 0.0) ||
                (csvRecord.get(BTColumns.DATE.getIndex()).equals(BTColumns.DATE.getName()));
    }

    /**
     * @param dayRecords Records per date
     * @return new
     */
    private OutputRecord findWithdrawlsPerDay(@NonNull final List<Record> dayRecords, Date date) {
        if (dayRecords.isEmpty()) return null;
        return new OutputRecord(date, "",
                findMin(dayRecords),
                findMax(dayRecords),
                findAvg(dayRecords));
    }

    private Set<OutputRecord> findWithdrawlsByDescription(final List<Record> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return new HashSet<>();

        Set<OutputRecord> outputRecords = new HashSet<>();
        dayRecords.forEach(record -> {
            List<Record> records = dayRecords.stream()
                    .filter(r -> r.getDescription().equals(record.getDescription()))
                    .toList();

            outputRecords.add(new OutputRecord(
                    record.getDate(),
                    record.getDescription(),
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
