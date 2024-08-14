package com.example.Banking_system.service.impl;

import com.example.Banking_system.dto.EmailDetails;
import com.example.Banking_system.dto.TransactionDTO;
import com.example.Banking_system.entity.Transaction;
import com.example.Banking_system.entity.User;
import com.example.Banking_system.repository.TransactionRepository;
import com.example.Banking_system.repository.UserRepository;
import com.example.Banking_system.service.EmailService;
import com.example.Banking_system.service.TransactionService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;

    private static final String FILE = "D:\\MyStatement.pdf";
    @Override
    public void saveTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDTO.getTransactionType())
                .accountNumber(transactionDTO.getAccountNumber())
                .amount(transactionDTO.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully!");
    }

    @Override
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) {
        /**
         *         LocalDate start  = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
         *         LocalDate end =  LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
         *         List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
         *                 .filter(transaction -> transaction.getCreatedAt().isEqual(start))
         *                 .filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();
         */
        User user = userRepository.findByAccountNumber(accountNumber);
        List<Transaction> transactionList =  transactionRepository.generateStatement(accountNumber, startDate, endDate);
        try {
            designStatement(transactionList, startDate,  endDate,  user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return transactionList;
    }

    public void designStatement(List<Transaction> transactions, String startDate, String endDate, User user) throws FileNotFoundException, DocumentException {
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        //Bank Info
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Demo Bank Application"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.LIGHT_GRAY);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("Tan Binh, Ho Chi Minh City"));
        bankAddress.setBorder(0);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        //Customer Info
        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell fromDate = new PdfPCell(new Phrase("Start Date: " + startDate ));
        fromDate.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell toDate =  new PdfPCell(new Phrase("End Date: " + endDate));
        toDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: "  + user.getAddress()));
        space.setBorder(0);

        statementInfo.addCell(fromDate);
        statementInfo.addCell(statement);
        statementInfo.addCell(toDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);
        //Transaction Table
        PdfPTable  transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.LIGHT_GRAY);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.LIGHT_GRAY);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.LIGHT_GRAY);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactions.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });


        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);
        document.close();

        //Send attachment
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached!")
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);
    }
}
