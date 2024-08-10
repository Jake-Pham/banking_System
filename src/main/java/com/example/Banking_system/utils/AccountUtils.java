package com.example.Banking_system.utils;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXIST_CODE = "001";
    public static final String ACCOUNT_EXIST_MESSAGE = "This user already has an account Created1";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";
    public static String generateAccountNumber(){
        /**
         * currentYear + randomSixDigits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        //Generate a random number between min and max
        int randNumber = (int)Math.floor(Math.random() * (max - min + 1) + min);
        //convert the current and randNumber to string, then concatenate
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        //
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}
