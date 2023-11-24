package com.mycode.datageniehack.datageniehack.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class StocksController {

    private static final String STOCKS_FILE = "E:\\Downloads\\stocks.xlsx";

    @GetMapping("/api/topstocks")
    public String getTopStocks() {
        try {
            FileInputStream stocksFile = new FileInputStream(new File(STOCKS_FILE));
            Workbook workbook = new XSSFWorkbook(stocksFile);

            Sheet sheet = workbook.getSheetAt(0);

            List<JSONObject> stocksData = new ArrayList<>();

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
                stocksData.add(rowData);
            }

            List<JSONObject> topStocks = getTopStocksByPurchasePrice(stocksData, 10);

            workbook.close();

            return new JSONArray(topStocks).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching top stocks";
        }
    }

    private List<JSONObject> getTopStocksByPurchasePrice(List<JSONObject> stocksData, int limit) {
        List<JSONObject> sortedByPurchasePrice = stocksData.stream()
                .sorted(Comparator.comparingDouble(stock -> stock.getDouble("PurchasePrice")))
                .collect(Collectors.toList());

        return sortedByPurchasePrice.stream().limit(limit).collect(Collectors.toList());
    }
}
