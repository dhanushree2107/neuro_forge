package com.neuroforge_nexus.controller;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import com.neuroforge_nexus.service.KafkaEventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final KafkaEventService kafkaEventService;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository,
                          SprintRepository sprintRepository, KafkaEventService kafkaEventService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.sprintRepository = sprintRepository;
        this.kafkaEventService = kafkaEventService;
    }

    @PostMapping("/create")
    public String createTask(@RequestParam("title") String title,
                             @RequestParam("taskKey") String taskKey,
                             @RequestParam("points") Integer points,
                             @RequestParam("sprintId") Long sprintId,
                             @RequestParam("assigneeId") Long assigneeId,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/sprints?error=unauthorized";
        }

        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow();
        User assignee = userRepository.findById(assigneeId).orElse(null);

        Task task = Task.builder()
                .taskKey(taskKey.toUpperCase())
                .title(title)
                .points(points)
                .status("To Do")
                .sprint(sprint)
                .assignee(assignee)
                .isBlocked(false)
                .blockerReason("")
                .blockerStatus("None")
                .build();

        taskRepository.save(task);

        // Publish to Mock Kafka
        String msg = String.format("Task %s ('%s') was created and assigned to %s.", 
                task.getTaskKey(), task.getTitle(), assignee != null ? assignee.getUsername() : "Unassigned");
        kafkaEventService.publishTaskEvent(task.getTaskKey(), "TASK_CREATED", msg);

        return "redirect:/sprints?success=task_created";
    }

    @PostMapping("/move")
    public String moveTask(@RequestParam("taskId") Long taskId,
                           @RequestParam("status") String status,
                           Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Task task = taskRepository.findById(taskId).orElseThrow();
        String oldStatus = task.getStatus();
        task.setStatus(status);
        taskRepository.save(task);

        // Publish to Mock Kafka
        String msg = String.format("Task %s status was changed from %s to %s.", 
                task.getTaskKey(), oldStatus, status);
        kafkaEventService.publishTaskEvent(task.getTaskKey(), "TASK_MOVED", msg);

        return "redirect:/sprints?success=task_moved";
    }

    @PostMapping("/block")
    public String blockTask(@RequestParam("taskId") Long taskId,
                            @RequestParam(value = "isBlocked", defaultValue = "false") boolean isBlocked,
                            @RequestParam(value = "blockerReason", required = false) String blockerReason,
                            @RequestParam(value = "blockerStatus", required = false) String blockerStatus,
                            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/sprints?error=unauthorized";
        }

        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setBlocked(isBlocked);
        if (isBlocked) {
            task.setBlockerReason(blockerReason);
            task.setBlockerStatus(blockerStatus);
            String msg = String.format("Task %s was flagged as BLOCKED. Reason: %s. Status: %s.", 
                    task.getTaskKey(), blockerReason, blockerStatus);
            kafkaEventService.publishTaskEvent(task.getTaskKey(), "TASK_BLOCKED", msg);
        } else {
            task.setBlockerReason("");
            task.setBlockerStatus("None");
            String msg = String.format("Task %s was unblocked.", task.getTaskKey());
            kafkaEventService.publishTaskEvent(task.getTaskKey(), "TASK_UNBLOCKED", msg);
        }
        taskRepository.save(task);

        return "redirect:/sprints?success=task_blocked_updated";
    }
}
