package com.mycode.datageniehack.datageniehack.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
public class WealthiestCustomerController {
    private List<JSONObject> readDataFromSheet(String filename) {
        try {
            FileInputStream mutualFundsFile = new FileInputStream(new File(filename));
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

            // Close the workbook
            workbook.close();

            return mutualFundsData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to calculate total wealth of each customer based on their assets
    // Method to calculate total wealth of each customer based on their assets
    private Map<Integer, Double> calculateTotalWealth(
            List<JSONObject> accountData,
            List<JSONObject> mutualFundsData,
            List<JSONObject> stocksData,
            List<JSONObject> fixedDepositsData
    ) {
        Map<Integer, Double> customerIdToTotalWealth = new HashMap<>();

        // Process accountData, mutualFundsData, stocksData, fixedDepositsData to calculate total wealth
        // You need to match AccountID or CustomerID to calculate the total wealth
        // Iterate through the data and aggregate the total wealth per CustomerID

        // Example logic to calculate total wealth
        for (JSONObject account : accountData) {
            int customerId = account.getInt("CUSTOMERID");
            double accountBalance = account.getDouble("ACCOUNTBALANCE");
//            double mutualFunds=mutualFundsData.getDouble("INVESTMENTAMOUNT");

            // Assuming you have similar fields for mutual funds, stocks, and fixed deposits
            // Fetch related data from mutualFundsData, stocksData, fixedDepositsData based on AccountID or CustomerID

            // Perform necessary calculations to get the total wealth
            double totalWealth = accountBalance /* Add wealth from mutual funds, stocks, fixed deposits, etc. */;

            // Update the total wealth for the customer
            customerIdToTotalWealth.merge(customerId, totalWealth, Double::sum);
        }

        return customerIdToTotalWealth;
    }


    // Method to find the wealthiest customer and return their details including wealth
    private JSONObject findWealthiestCustomer(Map<Integer, Double> customerIdToTotalWealth,
                                              List<JSONObject> customerData,
                                              List<JSONObject> accountData,
                                              List<JSONObject> mutualFundsData,
                                              List<JSONObject> stocksData,
                                              List<JSONObject> fixedDepositsData) {
        JSONObject wealthiestCustomerDetails = new JSONObject();

        // Find the customer with the highest wealth
        double maxWealth = Collections.max(customerIdToTotalWealth.values());
        int wealthiestCustomerId = -1;

        for (Map.Entry<Integer, Double> entry : customerIdToTotalWealth.entrySet()) {
            if (entry.getValue().equals(maxWealth)) {
                wealthiestCustomerId = entry.getKey();
                break;
            }
        }

        // Get the details of the wealthiest customer based on CustomerID
        if (wealthiestCustomerId != -1) {
            JSONObject wealthiestCustomer = getCustomerDetails(wealthiestCustomerId, customerData);
            // Add wealth to the wealthiest customer details
            wealthiestCustomer.put("Wealth", maxWealth);

            wealthiestCustomerDetails = wealthiestCustomer;
        }

        return wealthiestCustomerDetails;
    }

    private JSONObject getCustomerDetails(int customerId, List<JSONObject> customerData) {
        // Find the customer in the list based on the CustomerID
        JSONObject customerDetails = new JSONObject();

        for (JSONObject customer : customerData) {
            int customerID = customer.getInt("CUSTOMERID");
            if (customerID == customerId) {
                // Assuming the customer details contain fields like FirstName, LastName, etc.
                String firstName = customer.getString("FIRSTNAME");
                String lastName = customer.getString("LASTNAME");
                String email=customer.getString("EMAIL");
                String phone=customer.getString("PHONE");
//                double totalWealth +=


                // Populate customer details into a JSONObject
                customerDetails.put("CustomerID", customerId);
                customerDetails.put("FirstName", firstName);
                customerDetails.put("LastName", lastName);
                customerDetails.put("email",email);
                customerDetails.put("phone",phone);
//                customerDetails.put("Wealth", wealth); // Include wealth or any other relevant details

                break; // Stop searching once the customer is found
            }
        }
        return customerDetails;
    }
    private static final String ACCOUNTS_SHEET = "Accounts Table"; // Sheet name for Accounts

    @GetMapping("/api/wealthiestcustomer")
    public String getWealthiestCustomer() {
        try {
            List<JSONObject> accountData = new ArrayList<>();
            List<JSONObject> mutualFundsData = new ArrayList<>();
            List<JSONObject> stocksData = new ArrayList<>();
            List<JSONObject> fixedDepositsData = new ArrayList<>();
            List<JSONObject> customerData = new ArrayList<>();
            // Read account data from Accounts.xlsx
            accountData = readDataFromSheet("E:\\Downloads\\accounts.xlsx");
            mutualFundsData = readDataFromSheet("E:\\Downloads\\mutual_funds.xlsx");
            stocksData = readDataFromSheet("E:\\Downloads\\stocks.xlsx");
            fixedDepositsData = readDataFromSheet("E:\\Downloads\\fixed_deposits.xlsx");
            customerData = readDataFromSheet("E:\\Downloads\\customers.xlsx");

            // Calculate total wealth of each customer based on their assets
            Map<Integer, Double> customerIdToWealth = calculateTotalWealth(accountData, mutualFundsData, stocksData, fixedDepositsData);

            // Find the wealthiest customer
            JSONObject wealthiestCustomerId = findWealthiestCustomer(customerIdToWealth,customerData,accountData,mutualFundsData, stocksData, fixedDepositsData);

            return wealthiestCustomerId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching wealthiest customer";
        }
    }
}
