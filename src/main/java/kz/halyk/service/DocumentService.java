package kz.halyk.service;

import kz.halyk.model.OutputRecord;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
public final class DocumentService {
    BufferedWriter writer;
    List<String> columns = List.of("Date", "Type", "Min", "Max", "Avg");

    public DocumentService(@NonNull String outputPath) {
        if (outputPath.charAt(0) != '.')
            outputPath = refactorFilePath(outputPath);
        new File(outputPath).deleteOnExit();
        try {
            writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(convertToCSV(columns).toCharArray());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(Set<OutputRecord> recordList) {
        recordList.forEach(outputRecord -> {
            try {
                writer.write(convertToCSV(outputRecord).toCharArray());
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void write(OutputRecord record) {
        try {
            writer.write(convertToCSV(record).toCharArray());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToCSV(OutputRecord data) {
        return String.valueOf(data.getDate()) + ',' +
                data.getType() + ',' +
                data.getMin() + ',' +
                data.getMax() + ',' +
                data.getAverage();
    }

    private String convertToCSV(List<String> data) {
        return String.join(",", data);
    }

    private static String refactorFilePath(String filePath) {
        return filePath.replaceAll("/", "\\");
    }
}
