package com.fingoal.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.fingoal.backend.user.AuthDtos.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal User user) {
        return UserProfileResponse.from(user);
    }

    @PatchMapping("/me")
    public UserProfileResponse updateMe(@AuthenticationPrincipal User user, @RequestBody UpdateProfileRequest request) {
        return authService.updateProfile(user.getId(), request);
    }
}
