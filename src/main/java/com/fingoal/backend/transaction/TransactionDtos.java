package com.fingoal.backend.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public class TransactionDtos {

    public record CreateTransactionRequest(
            @NotBlank String merchant,
            @NotBlank String category,
            @NotNull @Positive Double amount,
            @NotNull TransactionType type,
            @NotNull LocalDate date,
            String note
    ) {}

    public record TransactionResponse(
            String id,
            String merchant,
            String category,
            Double amount,
            TransactionType type,
            LocalDate date,
            String note
    ) {
        public static TransactionResponse from(Transaction t) {
            return new TransactionResponse(
                    t.getId().toString(), t.getMerchant(), t.getCategory(),
                    t.getAmount(), t.getType(), t.getDate(), t.getNote()
            );
        }
    }
}
