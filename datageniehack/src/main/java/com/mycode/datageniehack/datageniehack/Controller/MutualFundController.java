package com.mycode.datageniehack.datageniehack.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MutualFundController {

    @GetMapping("/api/topmutualfunds")
    public String getTopMutualFunds() {
        try {
            // Load Excel file (Update the file path)
            InputStream excelFile = new FileInputStream(new File("E:\\Downloads\\mutual_funds.xlsx"));
            Workbook workbook = new XSSFWorkbook(excelFile);

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            List<JSONObject> mutualFundData = new ArrayList<>();

            // Read headers
            Row headerRow = sheet.getRow(0);
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                headers[i] = headerRow.getCell(i).getStringCellValue();
            }

            // Iterate through rows (starting from row 1 as row 0 is the header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                JSONObject rowData = new JSONObject();
                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData.put(headers[j], cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    rowData.put(headers[j], cell.getDateCellValue().toString());
                                } else {
                                    rowData.put(headers[j], cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                rowData.put(headers[j], cell.getBooleanCellValue());
                                break;
                            case BLANK:
                                rowData.put(headers[j], "BLANK");
                                break;
                            default:
                                rowData.put(headers[j], "UNKNOWN");
                                break;
                        }
                    } else {
                        rowData.put(headers[j], JSONObject.NULL);
                    }
                }
                mutualFundData.add(rowData);
            }

            // Implement logic to determine top-performing mutual funds
            List<JSONObject> topMutualFunds = getTopPerformingMutualFunds(mutualFundData, 10);

            // Close the workbook
            workbook.close();

            return new JSONArray(topMutualFunds).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching top mutual funds";
        }
    }

    // Logic to determine top-performing mutual funds
    private List<JSONObject> getTopPerformingMutualFunds(List<JSONObject> mutualFundsData, int limit) {
        // Implement logic to calculate performance metrics and identify top mutual funds
        // Modify this section to determine top-performing mutual funds based on criteria

        // Example: Sorting by NAV
        List<JSONObject> sortedByNAV = mutualFundsData.stream()
                .sorted(Comparator.comparingDouble(mf -> mf.getDouble("NAV")))
                .collect(Collectors.toList());

        return sortedByNAV.stream().limit(limit).collect(Collectors.toList());
    }
}
