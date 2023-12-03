package com.mycode.datageniehack.datageniehack.Controller;
import com.mycode.datageniehack.datageniehack.Service.FixedDepositsService;
import com.mycode.datageniehack.datageniehack.Service.StocksService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExcelController {
    @GetMapping("/")
    public String helloWorld(){
        return JSONObject.quote("Hello World corss app");
    }
    @GetMapping("/api/topwealthiest")
    public String getTopWealthiest() {
        try {
            // Load Excel file
            InputStream excelFile = new FileInputStream(new File("E:\\Downloads\\accounts.xlsx"));
            Workbook workbook = new XSSFWorkbook(excelFile);

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            List<JSONObject> excelData = new ArrayList<>();

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
                excelData.add(rowData);
            }

            // Sort users by account balance
            List<JSONObject> sortedUsers = excelData.stream()
                    .filter(user -> user.has("ACCOUNTBALANCE") && user.get("ACCOUNTBALANCE") instanceof Number)
                    .sorted(Comparator.comparingDouble(user -> user.getDouble("ACCOUNTBALANCE")))
                    .collect(Collectors.toList());

            // Get top 10 wealthiest users
            List<JSONObject> top10Wealthiest = sortedUsers.stream().limit(10).collect(Collectors.toList());

            // Close the workbook
            workbook.close();

            return new JSONArray(top10Wealthiest).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching top wealthiest users";
        }
    }
}
