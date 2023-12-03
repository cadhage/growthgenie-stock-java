package com.mycode.datageniehack.datageniehack.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Service
public class HistoricalReturnsService {
    public List<JSONObject> getCustomerTransactionsSQLRequest() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<JSONObject> jsonObjectList = new ArrayList<>();

        try {
            // Establish the database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdatabase", "root", "sweety44");

            // Create SQL query
            String sql = "SELECT \n" +
                    "    c.CustomerID,\n" +
                    "    c.FirstName,\n" +
                    "    c.LastName,\n" +
                    "    t.TransactionDate,\n" +
                    "    t.Amount,\n" +
                    "    t.TransactionType\n" +
                    "FROM Customers c\n" +
                    "JOIN (\n" +
                    "    SELECT \n" +
                    "        c.CustomerID,\n" +
                    "        COALESCE(SUM(a.AccountBalance), 0) \n" +
                    "        + COALESCE(SUM(s.Quantity * s.CurrentPrice), 0) \n" +
                    "        + COALESCE(SUM(mf.InvestmentAmount), 0) \n" +
                    "        + COALESCE(SUM(fd.MaturityAmount), 0) AS TotalAssets\n" +
                    "    FROM Customers c\n" +
                    "    LEFT JOIN Accounts a ON c.CustomerID = a.CustomerID\n" +
                    "    LEFT JOIN InvestmentAccounts ia ON c.CustomerID = ia.CustomerID\n" +
                    "    LEFT JOIN Stocks s ON ia.InvestmentAccountID = s.InvestmentAccountID\n" +
                    "    LEFT JOIN MutualFunds mf ON ia.InvestmentAccountID = mf.InvestmentAccountID\n" +
                    "    LEFT JOIN FixedDeposits fd ON ia.InvestmentAccountID = fd.InvestmentAccountID\n" +
                    "    GROUP BY c.CustomerID\n" +
                    "    ORDER BY TotalAssets DESC\n" +
                    "    LIMIT 5\n" +
                    ") top_customers ON c.CustomerID = top_customers.CustomerID\n" +
                    "LEFT JOIN Accounts a ON c.CustomerID = a.CustomerID\n" +
                    "LEFT JOIN Transactions t ON a.AccountID = t.AccountID\n" +
                    "WHERE t.TransactionDate BETWEEN DATE_SUB(NOW(), INTERVAL 24 MONTH) AND NOW()\n" +
                    "ORDER BY c.CustomerID, t.TransactionDate";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                int customerId = resultSet.getInt("CustomerID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                Date transactionDate = resultSet.getDate("TransactionDate");
                double amount = resultSet.getDouble("Amount");
                String transactionType = resultSet.getString("TransactionType");

                // Create JSON object
                JSONObject customerTransactionJson = new JSONObject();
                customerTransactionJson.put("CustomerID", customerId);
                customerTransactionJson.put("FirstName", firstName);
                customerTransactionJson.put("LastName", lastName);
                customerTransactionJson.put("TransactionDate", transactionDate.toString());
                customerTransactionJson.put("Amount", amount);
                customerTransactionJson.put("TransactionType", transactionType);

                jsonObjectList.add(customerTransactionJson);
            }
//            writeToJSONFiles(jsonObjectList);
            return jsonObjectList;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Close connections and statements
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public List<JSONObject> getCustomerTransactions() {
        List<List<JSONObject>> responseObjectLi=new ArrayList<>();
        List<JSONObject> transactionObject= getCustomerTransactionsSQLRequest();
        List<JSONObject> responseObject= new ArrayList<>();
        // Separate transactions by CustomerID
        for (JSONObject transaction : transactionObject) {
            responseObject.add(transaction);
//        System.out.println(transaction);
        }
        responseObjectLi.add(responseObject);
        return responseObject;
    }
}
