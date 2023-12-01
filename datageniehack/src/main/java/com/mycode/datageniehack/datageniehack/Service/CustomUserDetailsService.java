package com.mycode.datageniehack.datageniehack.Service;

import com.mycode.datageniehack.datageniehack.Entity.User;
import com.mycode.datageniehack.datageniehack.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService {
    List<JSONObject> taxLiability=new ArrayList<>();
    public List<JSONObject> getWealthiestCustomer() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<JSONObject> jsonObjectList = new ArrayList<>();

        try {
            // Establish the database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdatabase", "root", "sweety44");

            // Create SQL query
            String sql = "SELECT \n" +
                    "    c.CustomerID, \n" +
                    "    c.FirstName, \n" +
                    "    c.LastName, \n" +
                    "    COALESCE(SUM(a.AccountBalance), 0) AS TotalAccountBalance,\n" +
                    "    COALESCE(SUM(s.Quantity * s.CurrentPrice), 0) AS TotalStockValue,\n" +
                    "    COALESCE(SUM(mf.InvestmentAmount), 0) AS TotalMutualFundValue,\n" +
                    "    COALESCE(SUM(fd.MaturityAmount), 0) AS TotalFixedDepositValue,\n" +
                    "    (\n" +
                    "        COALESCE(SUM(a.AccountBalance), 0) \n" +
                    "        + COALESCE(SUM(s.Quantity * s.CurrentPrice), 0)\n" +
                    "        + COALESCE(SUM(mf.InvestmentAmount), 0)\n" +
                    "        + COALESCE(SUM(fd.MaturityAmount), 0)\n" +
                    "    ) AS TotalAssets\n" +
                    "FROM Customers c\n" +
                    "LEFT JOIN Accounts a ON c.CustomerID = a.CustomerID\n" +
                    "LEFT JOIN InvestmentAccounts ia ON c.CustomerID = ia.CustomerID\n" +
                    "LEFT JOIN Stocks s ON ia.InvestmentAccountID = s.InvestmentAccountID\n" +
                    "LEFT JOIN MutualFunds mf ON ia.InvestmentAccountID = mf.InvestmentAccountID\n" +
                    "LEFT JOIN FixedDeposits fd ON ia.InvestmentAccountID = fd.InvestmentAccountID\n" +
                    "GROUP BY c.CustomerID, c.FirstName, c.LastName\n" +
                    "ORDER BY TotalAssets DESC\n" +
                    "LIMIT 5";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                int customerId = resultSet.getInt("CustomerID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                double totalAccountBalance = resultSet.getDouble("TotalAccountBalance");
                double totalStockValue = resultSet.getDouble("TotalStockValue");
                double totalMutualFundValue = resultSet.getDouble("TotalMutualFundValue");
                double totalFixedDepositValue = resultSet.getDouble("TotalFixedDepositValue");
                double totalAssets = resultSet.getDouble("TotalAssets");
//                String transactionType = resultSet.getString("TransactionType");


                // Create JSON object
                JSONObject customerTransactionJson = new JSONObject();
                customerTransactionJson.put("CustomerID", customerId);
                customerTransactionJson.put("FirstName", firstName);
                customerTransactionJson.put("LastName", lastName);
                customerTransactionJson.put("TotalAccountBalance", totalAccountBalance);
                customerTransactionJson.put("TotalStockValue", totalStockValue);
                customerTransactionJson.put("TotalMutualFundValue", totalMutualFundValue);
                customerTransactionJson.put("TotalFixedDepositValue", totalFixedDepositValue);
                customerTransactionJson.put("TotalAssets", totalAssets);

                jsonObjectList.add(customerTransactionJson);
            }
//            writeToJSONFiles(jsonObjectList);
            System.out.println(jsonObjectList);
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
}
