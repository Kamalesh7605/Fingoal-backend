package com.fingoal.backend.dashboard;

import com.fingoal.backend.transaction.Transaction;
import com.fingoal.backend.transaction.TransactionDtos.TransactionResponse;
import com.fingoal.backend.transaction.TransactionRepository;
import com.fingoal.backend.transaction.TransactionType;
import com.fingoal.backend.user.User;
import com.fingoal.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.fingoal.backend.dashboard.DashboardDtos.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardSummary buildSummary(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();

        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        List<Transaction> monthTxns = transactionRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, monthStart, monthEnd);

        double income = monthTxns.stream()
                .filter(t -> t.getType() == TransactionType.credit)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double spent = monthTxns.stream()
                .filter(t -> t.getType() == TransactionType.debit)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double saved = income - spent;

        Map<String, Double> byCategory = monthTxns.stream()
                .filter(t -> t.getType() == TransactionType.debit)
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));

        List<CategoryBreakdown> topCategories = byCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(e -> new CategoryBreakdown(
                        e.getKey(),
                        e.getValue(),
                        spent > 0 ? Math.round((e.getValue() / spent) * 1000.0) / 10.0 : 0.0
                ))
                .toList();

        List<TransactionResponse> recent = transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(10)
                .map(TransactionResponse::from)
                .toList();

        Double currentSavingsBalance = user.getCurrentSavings();

        return new DashboardSummary(income, spent, saved, currentSavingsBalance, topCategories, recent);
    }
}
