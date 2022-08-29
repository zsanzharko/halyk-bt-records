package kz.halyk.service;

import kz.halyk.App;
import kz.halyk.model.OutputRecord;
import kz.halyk.model.TrimRecord;
import kz.halyk.utils.CSVUtil;
import kz.halyk.utils.ProfileTimer;
import kz.halyk.utils.enums.BTColumns;
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
public class ComputingService {
    private final List<OutputRecord> recordList = new ArrayList<>(10000);

    ProfileTimer timer = new ProfileTimer("Computer Service");

    public void fastCompute(String filePath) throws IOException, ParseException {
        timer.start();

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
                        recordList.addAll(fastFindWithdrawlsByDescription(dayRecords));
                        recordList.add(fastFindWithdrawlsData(dayRecords));
                    } catch (NullPointerException ignore) {
                    }
                }
                currentDate = model.getDate();
                dayRecords.clear();
                dayRecords.add(model);
            }
        }
        timer.stop();
        timer.start();
        CSVUtil.writeRecords(recordList);
        timer.stop();

        timer.getResults();
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

    private List<OutputRecord> fastFindWithdrawlsByDescription(final List<TrimRecord> dayRecords) {
        if (dayRecords == null || dayRecords.isEmpty()) return new ArrayList<>();
        List<OutputRecord> outputRecords = new ArrayList<>();
        dayRecords.forEach(record -> {
            String description = record.getDescription();
            var records = dayRecords.stream()
                    .filter(record1 -> record1.getDescription().equals(description))
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

    private BigDecimal findMin(List<TrimRecord> records) {
        return  records.stream()
                .map(TrimRecord::getWithdrawal)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal findAvg(List<TrimRecord> records) {
        return BigDecimal.valueOf(records.stream()
                .map(TrimRecord::getWithdrawal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue() / records.size());
    }

    private BigDecimal findMax(List<TrimRecord> records) {
        return records.stream()
                .map(TrimRecord::getWithdrawal)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }
}
