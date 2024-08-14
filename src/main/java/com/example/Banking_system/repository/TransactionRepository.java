package com.example.Banking_system.repository;

import com.example.Banking_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query(value = "Select * from transaction where account_number = ? and  created_at >= ? and created_at <= ?",nativeQuery = true)
    List<Transaction> generateStatement(String accountNumber, String startDate, String endDate);
}
