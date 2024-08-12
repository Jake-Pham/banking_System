package com.example.Banking_system.service.impl;

import com.example.Banking_system.dto.*;
import com.example.Banking_system.entity.User;
import com.example.Banking_system.repository.UserRepository;
import com.example.Banking_system.service.EmailService;
import com.example.Banking_system.service.UserService;
import com.example.Banking_system.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Create an account - saving a new user into the db
         * Check if user already has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();
        User saveduser = userRepository.save(newUser);
        //Send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(saveduser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulation! Your account has been successfully Created. \nYour Account Detail: \n" +
                        "Account Name: " + saveduser.getFirstName() + " " + saveduser.getLastName() + " " + saveduser.getOtherName() +
                        "\nAccount Number:" + saveduser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(saveduser.getAccountBalance())
                        .accountNumber(saveduser.getAccountNumber())
                        .accountName(saveduser.getFirstName() + " " + saveduser.getLastName() + " " + saveduser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //Check if the provided account number exists
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        //Check if the provided account number exists
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist){
            return AccountUtils.ACCOUNT_FOUND_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //Check if the provided account number exists
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        //Add credit to account
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //Check if the provided account number exists
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        //Check if the amount you want to withdraw is no more than your bank balance
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount  = request.getAmount().toBigInteger();
        if(availableBalance.intValue() < debitAmount.intValue()){
            return  BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        //Withdraw debit from account
        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                        .accountBalance(userToDebit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();
        //Check if the accounts exists
        boolean isDestinationAccountExist  = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist ){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        //Alert source account user
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been deducted from your account! \nYour current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);
        //
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);
        //Alert destination account user
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been sent from your account from " + sourceUsername + "! \nYour current balance is " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
