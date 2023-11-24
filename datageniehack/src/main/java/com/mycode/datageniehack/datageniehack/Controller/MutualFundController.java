package com.mycode.datageniehack.datageniehack.Controller;//package com.mycode.datageniehack.datageniehack.Controller;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MutualFundController {

    private static final String MUTUAL_FUNDS_FILE = "E:\\Downloads\\mutual_funds.xlsx";

    @GetMapping("/api/topmutualfunds")
    public String getTopMutualFunds() {
        try {
            FileInputStream mutualFundsFile = new FileInputStream(new File(MUTUAL_FUNDS_FILE));
            Workbook workbook = new XSSFWorkbook(mutualFundsFile);

            // Accessing the first sheet assuming mutual funds data is on the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            List<JSONObject> mutualFundsData = new ArrayList<>();

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
                            default:
                                rowData.put(headers[j], "UNKNOWN");
                                break;
                        }
                    } else {
                        rowData.put(headers[j], JSONObject.NULL);
                    }
                }
                mutualFundsData.add(rowData);
            }

            // Logic to determine top mutual funds (e.g., based on highest NAV)
            List<JSONObject> topMutualFunds = getTopMutualFundsByNAV(mutualFundsData, 10);

            // Close the workbook
            workbook.close();

            return new JSONArray(topMutualFunds).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching top mutual funds";
        }
    }

    // Method to get top mutual funds based on highest NAV
    private List<JSONObject> getTopMutualFundsByNAV(List<JSONObject> mutualFundsData, int limit) {
        // Example logic: Sorting by NAV
        List<JSONObject> sortedByNAV = mutualFundsData.stream()
                .sorted(Comparator.comparingDouble(mf -> mf.getDouble("NAV")))
                .collect(Collectors.toList());

        // Return top 'limit' mutual funds with highest NAV
        return sortedByNAV.stream().limit(limit).collect(Collectors.toList());
    }
}
