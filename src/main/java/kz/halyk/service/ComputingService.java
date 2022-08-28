package kz.halyk.service;

import kz.halyk.App;
import kz.halyk.model.OutputRecord;
import kz.halyk.model.Record;
import kz.halyk.model.TrimRecord;
import kz.halyk.utils.CSVUtil;
import kz.halyk.utils.enums.BTColumns;
import me.tongfei.progressbar.ProgressBar;
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

public class ComputingService {
    private final List<Record> recordList;

    private static final Set<OutputRecord> outputRecords = new HashSet<>();

    public ComputingService(List<Record> recordList) {
        this.recordList = recordList;
    }

    public void compute() {
        if (recordList == null || recordList.isEmpty()) return;

        final List<Record> dayRecords = new ArrayList<>();
        Date currentDate = recordList.get(0).getDate();

        try (ProgressBar progressBar = new ProgressBar("Computing", recordList.size())) {
            progressBar.maxHint(-1);
            for (Record record : recordList) {
                if (record.getWithdrawal().intValue() == 0) continue;

                if (currentDate.equals(record.getDate()))
                    dayRecords.add(record);
                else {
                    findWithdrawlsByDescription(dayRecords);
                    var outputModel = findWithdrawlsData(dayRecords);
                    outputModel.setType("");
                    dayRecords.clear();
                    currentDate = record.getDate();
                    dayRecords.add(record);
                }
                progressBar.step();
            }
        }
    }

    private static OutputRecord findWithdrawlsData(final List<Record> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return new OutputRecord(null, "", null, null, null);

        BigDecimal min = dayRecords.stream()
                .map(Record::getWithdrawal)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal average = BigDecimal.valueOf(dayRecords.stream()
                .map(Record::getWithdrawal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue() / dayRecords.size());

        BigDecimal max = dayRecords.stream()
                .map(Record::getWithdrawal)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

//        System.out.printf("Min: %s | Avg: %s | Max: %s\n", min, average, max);
        return new OutputRecord(dayRecords.get(0).getDate(), dayRecords.get(0).getDescription(), min, max, average);
    }

    private static void findWithdrawlsByDescription(final List<Record> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return;
        dayRecords.forEach(record -> {
            String description = record.getDescription();
            var model = dayRecords.stream()
                    .filter(record1 -> record1.getDescription().equals(description))
                    .toList();
            outputRecords.add(findWithdrawlsData(model));
        });
    }


    public static void fastCompute(String filePath) throws IOException, ParseException {
        final long startTime = System.currentTimeMillis();

        Reader in = new FileReader(refactorFilePath(filePath));
        Iterator<CSVRecord> records = CSVFormat.EXCEL.parse(in).iterator();

        final List<TrimRecord> dayRecords = new ArrayList<>();
        Date currentDate = null;

        while (records.hasNext()) {
            var record = records.next();
            if (record.get(BTColumns.DATE.getIndex()).equals(BTColumns.DATE.getName())) continue;

            if (Double.parseDouble(cleanAmount(record.get(BTColumns.WITHDRAWALS.getIndex()))) == 0.0) continue;

            var model = new TrimRecord(
                    App.dateFormat.parse(record.get(BTColumns.DATE.getIndex())),
                    record.get(BTColumns.DESCRIPTION.getIndex()),
                    new BigDecimal(cleanAmount(record.get(BTColumns.WITHDRAWALS.getIndex()))));

            if (currentDate != null && currentDate.equals(model.getDate())) {
                dayRecords.add(model);
            } else {
                if (currentDate != null) {
                    try {
                        outputRecords.addAll(fastFindWithdrawlsByDescription(dayRecords));
                        outputRecords.add(fastFindWithdrawlsData(dayRecords));
                    } catch (NullPointerException ignore) {
                    }
                }
                currentDate = model.getDate();
                dayRecords.clear();
                dayRecords.add(model);
            }
        }

//        for (CSVRecord record : records) {
//            if (record.get(BTColumns.DATE.getIndex()).equals(BTColumns.DATE.getName())) continue;
//
//            if (Double.parseDouble(cleanAmount(record.get(BTColumns.WITHDRAWALS.getIndex()))) == 0.0) continue;
//
//            var model = new TrimRecord(
//                    App.dateFormat.parse(record.get(BTColumns.DATE.getIndex())),
//                    record.get(BTColumns.DESCRIPTION.getIndex()),
//                    new BigDecimal(cleanAmount(record.get(BTColumns.WITHDRAWALS.getIndex()))));
//
//            if (currentDate != null && currentDate.equals(model.getDate())) {
//                dayRecords.add(model);
//            } else {
//                if (currentDate != null) {
//                    try {
//                        outputRecords.addAll(fastFindWithdrawlsByDescription(dayRecords));
//                        outputRecords.add(fastFindWithdrawlsData(dayRecords));
//                    } catch (NullPointerException ignore) {
//                    }
//                }
//                currentDate = model.getDate();
//                dayRecords.clear();
//                dayRecords.add(model);
//            }
//        }
        final long end1Point = System.currentTimeMillis();
        System.out.printf("First Point: %s\n", end1Point - startTime);
        CSVUtil.writeRecords(outputRecords);
        final long end2Point = System.currentTimeMillis();
        System.out.printf("Second Point: %s\n", end2Point - startTime);
    }

    private static OutputRecord fastFindWithdrawlsData(final List<TrimRecord> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return null;

        BigDecimal min = dayRecords.stream()
                .map(TrimRecord::getWithdrawal)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal average = BigDecimal.valueOf(dayRecords.stream()
                .map(TrimRecord::getWithdrawal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue() / dayRecords.size());

        BigDecimal max = dayRecords.stream()
                .map(TrimRecord::getWithdrawal)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        return new OutputRecord(dayRecords.get(0).getDate(), "", min, max, average);
    }

    private static Set<OutputRecord> fastFindWithdrawlsByDescription(final List<TrimRecord> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return new HashSet<>();
        Set<OutputRecord> outputRecords = new HashSet<>();
        dayRecords.forEach(record -> {
            String description = record.getDescription();
            var model = dayRecords.stream()
                    .filter(record1 -> record1.getDescription().equals(description))
                    .toList();

            BigDecimal min = model.stream()
                    .map(TrimRecord::getWithdrawal)
                    .min(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);

            BigDecimal average = BigDecimal.valueOf(model.stream()
                    .map(TrimRecord::getWithdrawal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue() / model.size());

            BigDecimal max = model.stream()
                    .map(TrimRecord::getWithdrawal)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
            outputRecords.add(new OutputRecord(model.get(0).getDate(), model.get(0).getDescription(), min, max, average));
        });

        return  outputRecords;
    }

    public int getRecordSize() {
        return recordList.size();
    }
}
