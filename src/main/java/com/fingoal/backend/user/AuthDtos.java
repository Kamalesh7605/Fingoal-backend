package com.fingoal.backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank String name,
            @Email @NotBlank String email,
            @Size(min = 8, message = "Password must be at least 8 characters") String password,
            String phone
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {}

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            UserProfileResponse user
    ) {}

    public record UserProfileResponse(
            String id,
            String name,
            String email,
            String phone,
            Double monthlyIncome,
            Double monthlyFixedExpenses,
            Double monthlyEmi,
            Double currentSavings,
            Double emergencyFund,
            Double monthlySip,
            String mainGoal,
            boolean demo
    ) {
        public static UserProfileResponse from(User u) {
            return new UserProfileResponse(
                    u.getId().toString(), u.getName(), u.getEmail(), u.getPhone(),
                    u.getMonthlyIncome(), u.getMonthlyFixedExpenses(), u.getMonthlyEmi(),
                    u.getCurrentSavings(), u.getEmergencyFund(), u.getMonthlySip(),
                    u.getMainGoal(), u.isDemo()
            );
        }
    }

    public record UpdateProfileRequest(
            String name,
            String phone,
            Double monthlyIncome,
            Double monthlyFixedExpenses,
            Double monthlyEmi,
            Double currentSavings,
            Double emergencyFund,
            Double monthlySip,
            String mainGoal
    ) {}
}
