package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class ExcelReader {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    public static List<Map<String, String>> getDataFromExcel(Path filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (var file = new FileInputStream(filePath.toFile());
             var workbook = new XSSFWorkbook(file)) {

            var sheet = Optional.ofNullable(workbook.getSheet(sheetName))
                    .orElseThrow(() -> new IllegalArgumentException("Sheet with name " + sheetName + " does not exist in the Excel file."));

            var headerRow = Optional.ofNullable(sheet.getRow(0))
                    .orElseThrow(() -> new IllegalArgumentException("Header row is missing in the sheet " + sheetName));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                if (row == null) continue;

                var rowData = new HashMap<String, String>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    var cell = row.getCell(j);
                    var cellValue = getCellValue(cell);

                    var header = headerRow.getCell(j).getStringCellValue();
                    rowData.put(header, cellValue.trim());
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            fail("Failed to read Excel file: " + filePath);
        }

        return data;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
