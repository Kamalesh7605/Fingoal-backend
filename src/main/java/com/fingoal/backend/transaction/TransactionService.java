package com.fingoal.backend.transaction;

import com.fingoal.backend.common.ApiException;
import com.fingoal.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.fingoal.backend.transaction.TransactionDtos.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> listForUser(UUID userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional
    public TransactionResponse create(User user, CreateTransactionRequest request) {
        Transaction txn = Transaction.builder()
                .user(user)
                .merchant(request.merchant())
                .category(request.category())
                .amount(request.amount())
                .type(request.type())
                .date(request.date())
                .note(request.note())
                .build();

        transactionRepository.save(txn);
        return TransactionResponse.from(txn);
    }

    @Transactional
    public void delete(UUID userId, UUID transactionId) {
        if (!transactionRepository.existsByIdAndUserId(transactionId, userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        transactionRepository.deleteById(transactionId);
    }
}
