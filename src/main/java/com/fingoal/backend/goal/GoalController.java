package com.fingoal.backend.goal;

import com.fingoal.backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.fingoal.backend.goal.GoalDtos.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public List<GoalResponse> list(@AuthenticationPrincipal User user) {
        return goalService.listForUser(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody CreateGoalRequest request) {
        return goalService.create(user, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        goalService.delete(user.getId(), id);
    }
}
