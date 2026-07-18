package com.fingoal.backend.goal;

import com.fingoal.backend.common.ApiException;
import com.fingoal.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.fingoal.backend.goal.GoalDtos.*;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<GoalResponse> listForUser(UUID userId) {
        return goalRepository.findByUserId(userId).stream()
                .map(GoalResponse::from)
                .toList();
    }

    @Transactional
    public GoalResponse create(User user, CreateGoalRequest request) {
        Goal goal = Goal.builder()
                .user(user)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currentAmount(request.currentAmount() != null ? request.currentAmount() : 0.0)
                .monthlyContribution(request.monthlyContribution() != null ? request.monthlyContribution() : 0.0)
                .targetDate(request.targetDate())
                .icon(request.icon())
                .build();

        goalRepository.save(goal);
        return GoalResponse.from(goal);
    }

    @Transactional
    public void delete(UUID userId, UUID goalId) {
        if (!goalRepository.existsByIdAndUserId(goalId, userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Goal not found");
        }
        goalRepository.deleteById(goalId);
    }
}
