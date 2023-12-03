package com.mycode.datageniehack.datageniehack.Controller;

import com.mycode.datageniehack.datageniehack.Service.FixedDepositsService;
import com.mycode.datageniehack.datageniehack.Service.StocksService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FixedDepositsController {

    private static final String FIXED_DEPOSITS_FILE = "E:\\Downloads\\fixed_deposits.xlsx";
    @Autowired
    FixedDepositsService fixedDepositsService;
    @GetMapping("/get/api/top/FDs")
    public String getCustomerTransactions() {
        List<JSONObject> responseObject =fixedDepositsService.getTopPerformingFDs();
//        System.out.println(responseObject);
        return responseObject.toString();
    }
    @GetMapping("/api/topfixeddeposits")
    public String getTopFixedDeposits() {
        try {
            FileInputStream fixedDepositsFile = new FileInputStream(new File(FIXED_DEPOSITS_FILE));
            Workbook workbook = new XSSFWorkbook(fixedDepositsFile);

            Sheet sheet = workbook.getSheetAt(0);

            List<JSONObject> fixedDepositsData = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                headers[i] = headerRow.getCell(i).getStringCellValue();
            }

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
                fixedDepositsData.add(rowData);
            }

            List<JSONObject> topFixedDeposits = getTopFixedDepositsByMaturityAmount(fixedDepositsData, 10);

            workbook.close();

            return new JSONArray(topFixedDeposits).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching top fixed deposits";
        }
    }

    private List<JSONObject> getTopFixedDepositsByMaturityAmount(List<JSONObject> fixedDepositsData, int limit) {
        List<JSONObject> sortedByMaturityAmount = fixedDepositsData.stream()
                .sorted(Comparator.comparingDouble(fd -> fd.getDouble("MATURITYAMOUNT")))
                .collect(Collectors.toList());

        return sortedByMaturityAmount.stream().limit(limit).collect(Collectors.toList());
    }
}
