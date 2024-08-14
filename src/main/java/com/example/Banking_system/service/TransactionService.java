package com.example.Banking_system.service;

import com.example.Banking_system.dto.TransactionDTO;
import com.example.Banking_system.entity.Transaction;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.util.List;

public interface TransactionService {
    void saveTransaction(TransactionDTO transactionDTO);
    /**
     * Retrieve list of transaction within a date range given  an account number
     * Generate a PDF file of transaction
     * Send the PDF file via Email
     */
    List<Transaction> generateStatement(String accountNumber, String startDate, String endDate);
}
