package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public class ExcelReader {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    public static List<Map<String, String>> getDataFromExcel(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet with name " + sheetName + " does not exist in the Excel file.");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row is missing in the sheet " + sheetName);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = "";

                    if (cell != null) {
                        cellValue = switch (cell.getCellType()) {
                            case STRING -> cell.getStringCellValue();
                            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
                            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                            default -> "";
                        };
                    }

                    String header = headerRow.getCell(j).getStringCellValue();
                    rowData.put(header, cellValue != null ? cellValue.trim() : "");
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            fail("Failed to read Excel file: " + filePath);
        }

        return data;
    }
}
