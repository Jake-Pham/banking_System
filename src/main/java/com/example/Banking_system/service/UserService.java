package com.example.Banking_system.service;

import com.example.Banking_system.dto.BankResponse;
import com.example.Banking_system.dto.CreditDebitRequest;
import com.example.Banking_system.dto.EnquiryRequest;
import com.example.Banking_system.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}
