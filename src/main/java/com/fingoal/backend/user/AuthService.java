package com.fingoal.backend.user;

import com.fingoal.backend.common.ApiException;
import com.fingoal.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.fingoal.backend.user.AuthDtos.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .demo(false)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        String type = jwtService.extractTokenType(token);
        if (!"refresh".equals(type)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Not a refresh token");
        }
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unknown user"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired or invalid");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.name() != null) user.setName(request.name());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.monthlyIncome() != null) user.setMonthlyIncome(request.monthlyIncome());
        if (request.monthlyFixedExpenses() != null) user.setMonthlyFixedExpenses(request.monthlyFixedExpenses());
        if (request.monthlyEmi() != null) user.setMonthlyEmi(request.monthlyEmi());
        if (request.currentSavings() != null) user.setCurrentSavings(request.currentSavings());
        if (request.emergencyFund() != null) user.setEmergencyFund(request.emergencyFund());
        if (request.monthlySip() != null) user.setMonthlySip(request.monthlySip());
        if (request.mainGoal() != null) user.setMainGoal(request.mainGoal());

        return UserProfileResponse.from(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, UserProfileResponse.from(user));
    }
}
