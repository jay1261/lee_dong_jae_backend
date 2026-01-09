package com.dongjae.backend.transaction.repository;

import com.dongjae.backend.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
