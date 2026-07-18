package com.fingoal.backend.dashboard;

import java.util.List;

public class DashboardDtos {

    public record CategoryBreakdown(String category, Double amount, Double percentage) {}

    public record DashboardSummary(
            Double totalIncomeThisMonth,
            Double totalSpentThisMonth,
            Double totalSaved,
            Double currentSavingsBalance,
            List<CategoryBreakdown> topCategories,
            List<com.fingoal.backend.transaction.TransactionDtos.TransactionResponse> recentTransactions
    ) {}
}
