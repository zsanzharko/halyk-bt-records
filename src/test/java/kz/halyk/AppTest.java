package kz.halyk;

import junit.framework.TestCase;
import kz.halyk.service.ComputingService;
import kz.halyk.service.DocumentService;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

public class AppTest {
    private final String outputPath = "./src/test/resources/test_output_1.csv";
    private final String pathTestData = "./src/test/resources/test_data/test_data_1.csv";
    @Test
    public void readData() throws IOException {
        DocumentService documentService = new DocumentService(outputPath); // create service that working with getting and saving data to csv
        Iterator<CSVRecord> records = documentService.getData(pathTestData).iterator();

        Assert.assertNotNull(records);
    }

//    @Test
//    public void computeReadingTestData() throws IOException, ParseException {
//        DocumentService documentService = new DocumentService(outputPath); // create service that working with getting and saving data to csv
//
//        ComputingService service = new ComputingService(documentService);
//        service.fastCompute(pathTestData);
//    }
}