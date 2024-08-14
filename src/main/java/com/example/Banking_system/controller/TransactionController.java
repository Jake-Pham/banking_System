package com.example.Banking_system.controller;

import com.example.Banking_system.entity.Transaction;
import com.example.Banking_system.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bankStatement")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate){
        return transactionService.generateStatement(accountNumber, startDate,  endDate);
    }
}
