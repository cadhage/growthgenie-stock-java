package com.mycode.datageniehack.datageniehack.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixedDepositsService {
    public List<JSONObject> getTopPerformingFDs() {
        List<List<JSONObject>> responseObjectLi=new ArrayList<>();
        List<JSONObject> transactionObject= getTopPerformingFDsSQLRequest();
        List<JSONObject> responseObject= new ArrayList<>();
        System.out.println(transactionObject);
        for (JSONObject transaction : transactionObject) {
            responseObject.add(transaction);
        }
        responseObjectLi.add(responseObject);
        return responseObject;
    }
    public List<JSONObject> getTopPerformingFDsSQLRequest() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<JSONObject> jsonObjectList = new ArrayList<>();

        try {
            // Establish the database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdatabase", "root", "sweety44");

            // Create SQL query
            String sql = "SELECT \n" +
                    "    FixedDepositID, \n" +
                    "    PrincipalAmount, \n" +
                    "    InterestRate, \n" +
                    "    MaturityDate, \n" +
                    "    InterestPaymentFrequency, \n" +
                    "    MaturityAmount,\n" +
                    "    ((MaturityAmount - PrincipalAmount) / PrincipalAmount) * 100 AS PercentageGrowth\n" +
                    "FROM FixedDeposits\n" +
                    "ORDER BY PercentageGrowth DESC\n" +
                    "LIMIT 10";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                int fixedDepositID = resultSet.getInt("FixedDepositID");
                double principalAmount = resultSet.getDouble("PrincipalAmount");
                double interestRate = resultSet.getDouble("InterestRate");
                Date maturityDate = resultSet.getDate("MaturityDate");
                String interestPaymentFrequency = resultSet.getString("InterestPaymentFrequency");
                double  maturityAmount = resultSet.getDouble("MaturityAmount");
                double percentageGrowth = resultSet.getDouble("PercentageGrowth");

                // Create JSON object
                JSONObject customerTransactionJson = new JSONObject();
                customerTransactionJson.put("FixedDepositID", fixedDepositID);
                customerTransactionJson.put("PrincipalAmount", principalAmount);
                customerTransactionJson.put("InterestRate", interestRate);
                customerTransactionJson.put("MaturityDate", maturityDate);
                customerTransactionJson.put("InterestPaymentFrequency", interestPaymentFrequency);
                customerTransactionJson.put("MaturityAmount", maturityAmount);
                customerTransactionJson.put("PercentageGrowth", percentageGrowth);

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
