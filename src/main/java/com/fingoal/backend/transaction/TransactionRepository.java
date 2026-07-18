package com.fingoal.backend.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserIdOrderByDateDesc(UUID userId);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(UUID userId, LocalDate from, LocalDate to);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
