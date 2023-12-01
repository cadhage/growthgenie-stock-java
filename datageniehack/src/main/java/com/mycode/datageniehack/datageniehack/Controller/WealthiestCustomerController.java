package com.mycode.datageniehack.datageniehack.Controller;

import com.mycode.datageniehack.datageniehack.Service.CustomUserDetailsService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
// ... (other code remains the same)

    // Method to calculate total wealth of the customer based on their assets
    private double calculateTotalWealthForCustomer(int customerId, List<JSONObject> accountData,
                                                   List<JSONObject> mutualFundsData,
                                                   List<JSONObject> stocksData,
                                                   List<JSONObject> fixedDepositsData) {
        double totalWealth = 0.0;

        // Calculate total wealth based on AccountID or CustomerID from different asset data
        totalWealth += calculateTotalAccountWealth(customerId, accountData);
        totalWealth += calculateTotalMutualFundsWealth(customerId, mutualFundsData);
        totalWealth += calculateTotalStocksWealth(customerId, stocksData);
        totalWealth += calculateTotalFixedDepositsWealth(customerId, fixedDepositsData);

        return totalWealth;
    }

    // Method to calculate total wealth from accounts based on CustomerID
    private double calculateTotalAccountWealth(int customerId, List<JSONObject> accountData) {
        double totalAccountWealth = 0.0;

        for (JSONObject account : accountData) {
            int accountCustomerId = account.getInt("CustomerID");
            if (accountCustomerId == customerId) {
                double accountBalance = account.getDouble("AccountBalance");
                totalAccountWealth += accountBalance;
            }
        }
        return totalAccountWealth;
    }

    // Method to calculate total wealth from mutual funds based on CustomerID
    private double calculateTotalMutualFundsWealth(int customerId, List<JSONObject> mutualFundsData) {
        double totalMutualFundsWealth = 0.0;

        for (JSONObject mutualFund : mutualFundsData) {
            int investmentCustomerId = mutualFund.getInt("CustomerID");
            if (investmentCustomerId == customerId) {
                double investmentAmount = mutualFund.getDouble("InvestmentAmount");
                totalMutualFundsWealth += investmentAmount;
            }
        }
        return totalMutualFundsWealth;
    }

    // Method to calculate total wealth from stocks based on CustomerID
    private double calculateTotalStocksWealth(int customerId, List<JSONObject> stocksData) {
        double totalStocksWealth = 0.0;

        for (JSONObject stock : stocksData) {
            int investmentCustomerId = stock.getInt("CustomerID");
            if (investmentCustomerId == customerId) {
                double purchasePrice = stock.getDouble("PurchasePrice");
                int quantity = stock.getInt("Quantity");
                totalStocksWealth += (purchasePrice * quantity);
            }
        }
        return totalStocksWealth;
    }

    // Method to calculate total wealth from fixed deposits based on CustomerID
    private double calculateTotalFixedDepositsWealth(int customerId, List<JSONObject> fixedDepositsData) {
        double totalFixedDepositsWealth = 0.0;

        for (JSONObject fixedDeposit : fixedDepositsData) {
            int investmentCustomerId = fixedDeposit.getInt("CustomerID");
            if (investmentCustomerId == customerId) {
                double maturityAmount = fixedDeposit.getDouble("MaturityAmount");
                totalFixedDepositsWealth += maturityAmount;
            }
        }
        return totalFixedDepositsWealth;
    }

// ... (other code remains the same)

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
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @GetMapping("/api/topwealthiestcustomers")
    public String getTopWealthiestCustomers() {
//        try {
//            List<JSONObject> accountData = readDataFromSheet("E:\\Downloads\\accounts.xlsx");
//            List<JSONObject> mutualFundsData = readDataFromSheet("E:\\Downloads\\mutual_funds.xlsx");
//            List<JSONObject> stocksData = readDataFromSheet("E:\\Downloads\\stocks.xlsx");
//            List<JSONObject> fixedDepositsData = readDataFromSheet("E:\\Downloads\\fixed_deposits.xlsx");
//            List<JSONObject> customerData = readDataFromSheet("E:\\Downloads\\customers.xlsx");
//
//            // Calculate total wealth of each customer based on their assets
//            Map<Integer, Double> customerIdToWealth = calculateTotalWealth(accountData, mutualFundsData, stocksData, fixedDepositsData);
//
//            // Create a list of entries (customer ID and wealth) to sort the wealthiest customers
//            List<Map.Entry<Integer, Double>> sortedList = new ArrayList<>(customerIdToWealth.entrySet());
//            sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
//
//            // Retrieve the top 5 wealthiest customers
//            List<JSONObject> topWealthiestCustomers = new ArrayList<>();
//            int count = 0;
//            for (Map.Entry<Integer, Double> entry : sortedList) {
//                if (count >= 5) {
//                    break;
//                }
//                int customerId = entry.getKey();
//                double wealth = entry.getValue();
//                JSONObject wealthiestCustomer = getCustomerDetails(customerId, customerData);
//                wealthiestCustomer.put("Wealth", wealth);
//                topWealthiestCustomers.add(wealthiestCustomer);
//                count++;
//            }
//
//            return topWealthiestCustomers.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error fetching top wealthiest customers";
//        }
           return customUserDetailsService.getWealthiestCustomer().toString();
//           return null;

    }
//        @GetMapping("/api/topwealthiestcustomers/historicalreturns")
//        public List<JSONObject> getTopWealthiestCustomersHistoricalReturns() {
//            try {
//                // Read necessary data from the provided files
//                List<JSONObject> accountData = readDataFromSheet("E:\\Downloads\\accounts.xlsx");
//                List<JSONObject> mutualFundsData = readDataFromSheet("E:\\Downloads\\mutual_funds.xlsx");
//                List<JSONObject> stocksData = readDataFromSheet("E:\\Downloads\\stocks.xlsx");
//                List<JSONObject> fixedDepositsData = readDataFromSheet("E:\\Downloads\\fixed_deposits.xlsx");
//                List<JSONObject> customerData = readDataFromSheet("E:\\Downloads\\customers.xlsx");
//
//                // Calculate total wealth for each customer
//                Map<Integer, Double> customerIdToTotalWealth = calculateTotalWealth(
//                        accountData,
//                        mutualFundsData,
//                        stocksData,
//                        fixedDepositsData
//                );
//
//                // Get the top 5 wealthiest customers
//                List<Map.Entry<Integer, Double>> topWealthiestCustomers = customerIdToTotalWealth.entrySet().stream()
//                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                        .limit(5)
//                        .collect(Collectors.toList());
//
//                List<JSONObject> historicalReturns = new ArrayList<>();
//
//                // Calculate monthly returns for the top 5 wealthiest customers
//                for (Map.Entry<Integer, Double> entry : topWealthiestCustomers) {
//                    int customerId = entry.getKey();
//                    double totalWealth = entry.getValue();
//
//                    // Your logic to calculate monthly returns for each customer using their historical data
//                    // Calculate monthly returns for accounts, mutual funds, stocks, fixed deposits, etc.
//                    // Populate the historicalReturns list with JSONObjects containing monthly return data
//                    // Example:
//                    JSONObject customerReturnData = new JSONObject();
//                    customerReturnData.put("CustomerId", customerId);
//                    customerReturnData.put("TotalWealth", totalWealth);
//                    // Add monthly return data for each customer
//
//                    historicalReturns.add(customerReturnData);
//                }
//
//                // Process and return historical returns data
//                return historicalReturns;
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Handle exceptions accordingly
//                return new ArrayList<>();
//            }
//        }
@GetMapping("/api/topwealthiestcustomers/historicalreturns")
public List<JSONObject> getTopWealthiestCustomersHistoricalReturns() {
    try {
        // Read necessary data from the provided files
        List<JSONObject> accountData = readDataFromSheet("E:\\Downloads\\accounts.xlsx");
        List<JSONObject> mutualFundsData = readDataFromSheet("E:\\Downloads\\mutual_funds.xlsx");
        List<JSONObject> stocksData = readDataFromSheet("E:\\Downloads\\stocks.xlsx");
        List<JSONObject> fixedDepositsData = readDataFromSheet("E:\\Downloads\\fixed_deposits.xlsx");
        List<JSONObject> customerData = readDataFromSheet("E:\\Downloads\\customers.xlsx");
        List<JSONObject> transactionData=readDataFromSheet("E:\\Downloads\\transactions.xlsx");
        // Calculate total wealth for each customer
        Map<Integer, Double> customerIdToTotalWealth = calculateTotalWealth(
                accountData,
                mutualFundsData,
                stocksData,
                fixedDepositsData
        );

        // Get the top 5 wealthiest customers
        List<Map.Entry<Integer, Double>> topWealthiestCustomers = customerIdToTotalWealth.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toList());

        List<JSONObject> historicalReturns = new ArrayList<>();

        // Calculate monthly returns for the top 5 wealthiest customers
        for (Map.Entry<Integer, Double> entry : topWealthiestCustomers) {
            int customerId = entry.getKey();
            double totalWealth = entry.getValue();

            // Calculate monthly returns for each customer based on different asset types
            // Example: For accounts
            List<JSONObject> customerAccountData = filterDataByCustomerId(accountData, customerId);
            Map<String, Double> monthlyAccountReturns = calculateMonthlyReturnsForAccounts(customerAccountData,transactionData,customerId);

            // Similarly, calculate monthly returns for mutual funds, stocks, fixed deposits

            // Create a JSONObject to store monthly return data
            JSONObject customerReturnData = new JSONObject();
            customerReturnData.put("CustomerId", customerId);
            customerReturnData.put("TotalWealth", totalWealth);
            customerReturnData.put("MonthlyReturns_Accounts", monthlyAccountReturns);
            // Add other calculated monthly returns for different asset types

            historicalReturns.add(customerReturnData);
        }

        // Process and return historical returns data
        return historicalReturns;
    } catch (Exception e) {
        e.printStackTrace();
        // Handle exceptions accordingly
        return new ArrayList<>();
    }
}

    // Helper methods for filtering and calculating monthly returns for different asset types

    // Method to filter data by customer ID (for accounts, mutual funds, etc.)
    private List<JSONObject> filterDataByCustomerId(List<JSONObject> data, int customerId) {
        return data.stream()
                .filter(obj -> obj.getInt("CUSTOMERID") == customerId)
                .collect(Collectors.toList());
    }


    private Map<String, Double> calculateMonthlyReturnsForAccounts(List<JSONObject> customerAccountData, List<JSONObject> transactionData,int customerId) {
        Map<String, Double> monthlyReturns = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        String lastTransactionDateStr = customerAccountData.get(0).getString("LASTTRANSACTIONDATE");
        LocalDateTime lastTransactionDate = LocalDateTime.parse(lastTransactionDateStr, formatter);

        List<JSONObject> transactionsForCustomer = transactionData.stream()
                .filter(transaction -> transaction.getInt("ACCOUNTID") == customerId
                        && transaction.getString("TRANSACTIONSTATUS").equals("APPROVED"))
                .collect(Collectors.toList());

        // Group transactions by month
        Map<String, Double> totalAmountByMonth = new HashMap<>();
        for (JSONObject transaction : transactionsForCustomer) {
            LocalDateTime transactionDate = LocalDateTime.parse(transaction.getString("TRANSACTIONDATE"), formatter);
            String monthYear = transactionDate.getMonth().toString() + "-" + transactionDate.getYear();

            totalAmountByMonth.merge(monthYear, transaction.getDouble("AMOUNT"), Double::sum);
        }

        // Calculate monthly returns for the account
        List<String> months = new ArrayList<>(totalAmountByMonth.keySet());
        Collections.sort(months); // Sort months chronologically
        for (int i = 1; i < months.size(); i++) {
            String currentMonth = months.get(i);
            String previousMonth = months.get(i - 1);

            double currentAmount = totalAmountByMonth.get(currentMonth);
            double previousAmount = totalAmountByMonth.get(previousMonth);

            double monthlyReturn = ((currentAmount - previousAmount) / previousAmount) * 100.0;

            // Construct a unique identifier using customerId and monthYear
            String identifier = customerId + "-" + currentMonth;
            monthlyReturns.put(identifier, monthlyReturn);
        }

        return monthlyReturns;
    }





    // Methods for calculating monthly returns for other asset types (mutual funds, stocks, fixed deposits)

    // ... (other methods)
}
