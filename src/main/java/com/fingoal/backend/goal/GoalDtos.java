package com.fingoal.backend.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class GoalDtos {

    public record CreateGoalRequest(
            @NotBlank String name,
            @NotNull @PositiveOrZero Double targetAmount,
            @PositiveOrZero Double currentAmount,
            @PositiveOrZero Double monthlyContribution,
            @NotNull LocalDate targetDate,
            String icon
    ) {}

    public record GoalResponse(
            String id,
            String name,
            Double targetAmount,
            Double currentAmount,
            Double monthlyContribution,
            LocalDate targetDate,
            String icon,
            boolean onTrack,
            int monthsToGoal
    ) {
        public static GoalResponse from(Goal g) {
            double remaining = g.getTargetAmount() - g.getCurrentAmount();
            int monthsToGoal = g.getMonthlyContribution() != null && g.getMonthlyContribution() > 0
                    ? (int) Math.ceil(remaining / g.getMonthlyContribution())
                    : Integer.MAX_VALUE;

            long monthsUntilTarget = java.time.temporal.ChronoUnit.MONTHS.between(LocalDate.now(), g.getTargetDate());
            boolean onTrack = monthsToGoal <= Math.max(monthsUntilTarget, 0) + 1;

            return new GoalResponse(
                    g.getId().toString(), g.getName(), g.getTargetAmount(), g.getCurrentAmount(),
                    g.getMonthlyContribution(), g.getTargetDate(), g.getIcon(), onTrack, monthsToGoal
            );
        }
    }
}
