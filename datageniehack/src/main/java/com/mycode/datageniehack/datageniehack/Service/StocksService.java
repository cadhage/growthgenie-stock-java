package com.mycode.datageniehack.datageniehack.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class StocksService {

    public List<JSONObject> getTopPerformingStokes() {
        List<List<JSONObject>> responseObjectLi=new ArrayList<>();
        List<JSONObject> transactionObject= getTopPerformingStokesSQLRequest();
        List<JSONObject> responseObject= new ArrayList<>();
        // Separate transactions by CustomerID
        for (JSONObject transaction : transactionObject) {
            responseObject.add(transaction);
//        System.out.println(transaction);
        }
        responseObjectLi.add(responseObject);
        return responseObject;
    }
    public List<JSONObject> getTopPerformingStokesSQLRequest() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<JSONObject> jsonObjectList = new ArrayList<>();

        try {
            // Establish the database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdatabase", "root", "sweety44");

            // Create SQL query
            String sql = "\n" +
                    "SELECT \n" +
                    "    StockID,\n" +
                    "    StockName,\n" +
                    "    StockExchange,\n" +
                    "    CurrentPrice,\n" +
                    "    PurchasePrice,\n" +
                    "    Quantity,\n "+
                    "    ((CurrentPrice - PurchasePrice) / PurchasePrice) * 100 AS PriceChangePercentage\n" +
                    "FROM Stocks\n" +
                    "ORDER BY PriceChangePercentage DESC\n" +
                    "LIMIT 10";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                int stockId = resultSet.getInt("StockID");
                String stockName = resultSet.getString("StockName");
                String stockExchange = resultSet.getString("StockExchange");
                double currentPrice = resultSet.getDouble("CurrentPrice");
                double purchasePrice = resultSet.getDouble("PurchasePrice");
                int quantity = resultSet.getInt("Quantity");
                double priceChangePercentage = resultSet.getDouble("PriceChangePercentage");

                // Create JSON object
                JSONObject customerTransactionJson = new JSONObject();
                customerTransactionJson.put("StockID", stockId);
                customerTransactionJson.put("StockName", stockName);
                customerTransactionJson.put("StockExchange", stockExchange);
                customerTransactionJson.put("CurrentPrice", currentPrice);
                customerTransactionJson.put("PurchasePrice", purchasePrice);
                customerTransactionJson.put("Quantity", quantity);
                customerTransactionJson.put("PriceChangePercentage", priceChangePercentage);

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
}
