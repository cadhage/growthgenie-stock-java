package com.mycode.datageniehack.datageniehack.Controller;

import com.mycode.datageniehack.datageniehack.Service.HistoricalReturnsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class HistoricalReturnsController {
    @Autowired
    HistoricalReturnsService historicalReturnsService;
    @GetMapping("/get/customer/transactions")
    public String getCustomerTransactions() {
        List<JSONObject> responseObject =historicalReturnsService.getCustomerTransactions();
//        System.out.println(responseObject);
        return responseObject.toString();
    }
}
