package com.neuroforge_nexus.config;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final KafkaEventRepository kafkaEventRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, TeamRepository teamRepository,
                           ProjectRepository projectRepository, SprintRepository sprintRepository,
                           MilestoneRepository milestoneRepository, TaskRepository taskRepository,
                           KafkaEventRepository kafkaEventRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.milestoneRepository = milestoneRepository;
        this.taskRepository = taskRepository;
        this.kafkaEventRepository = kafkaEventRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Teams
        Team backendTeam = getOrCreateTeam("Backend", "Core backend development team");
        Team frontendTeam = getOrCreateTeam("Frontend", "User interface development team");
        Team qaTeam = getOrCreateTeam("QA", "Quality assurance and testing team");
        Team devopsTeam = getOrCreateTeam("DevOps", "Infrastructure and pipeline automation team");
        Team team12 = getOrCreateTeam("Team 12", "SDLC Integration Team 12");

        // 2. Seed Users (Notice dev's username is 'John' to match mockup screen)
        getOrCreateUser("admin", "admin@neuroforge.com", "admin123", Role.ADMIN, null);
        getOrCreateUser("pm", "pm@neuroforge.com", "pm123", Role.PROJECT_MANAGER, team12);
        User john = getOrCreateUser("John", "dev@neuroforge.com", "dev123", Role.DEVELOPER, backendTeam);
        getOrCreateUser("tester", "tester@neuroforge.com", "tester123", Role.TESTER, qaTeam);
        getOrCreateUser("devops", "devops@neuroforge.com", "devops123", Role.DEVOPS_ENGINEER, devopsTeam);

        // 3. Seed Projects
        Project finCoreNexus = getOrCreateProject("FinCore Nexus", "Active", team12, "Enterprise banking platform SDLC management system.");

        // 4. Seed Sprints (Sprint 12 updated with goal and daysLeft)
        Sprint sprint12 = getOrCreateSprint("Sprint 12", 25, 80, "Active", finCoreNexus, "Payment Service", 3);

        // 5. Seed Milestones
        getOrCreateMilestone("Release 2.3", LocalDate.of(2026, 6, 20), "Active", finCoreNexus);

        // 6. Seed Tasks for Sprint 12 (5 To Do, 8 In Progress, 12 Done)
        if (taskRepository.count() == 0) {
            // Seed Blocked Task PAY-247 (In Progress, Blocked, assigned to John)
            taskRepository.save(Task.builder()
                    .taskKey("PAY-247")
                    .title("Payment API")
                    .description("Core payment collection and gateway dispatch API.")
                    .points(5)
                    .status("In Progress")
                    .assignee(john)
                    .sprint(sprint12)
                    .isBlocked(true)
                    .blockerReason("DB migration pending")
                    .blockerStatus("Escalated")
                    .build());

            // 4 more To Do tasks (Total 5 To Do)
            taskRepository.save(createMockTask("PAY-248", "Payment Gateway Integration", 3, "To Do", john, sprint12));
            taskRepository.save(createMockTask("PAY-249", "Card Validation Service", 5, "To Do", john, sprint12));
            taskRepository.save(createMockTask("PAY-250", "Receipt Generation Integration", 2, "To Do", john, sprint12));
            taskRepository.save(createMockTask("PAY-251", "Refund Processing Controller", 8, "To Do", john, sprint12));

            // 7 more In Progress tasks (Total 8 In Progress)
            taskRepository.save(createMockTask("PAY-252", "Third-Party Wallet Sync", 5, "In Progress", john, sprint12));
            taskRepository.save(createMockTask("PAY-253", "Database Schema Validation", 3, "In Progress", john, sprint12));
            taskRepository.save(createMockTask("PAY-254", "Error Code Mapping", 2, "In Progress", john, sprint12));
            taskRepository.save(createMockTask("PAY-255", "Kafka Event Emitter", 5, "In Progress", john, sprint12));
            taskRepository.save(createMockTask("PAY-256", "API Gateway Routing", 3, "In Progress", john, sprint12));
            taskRepository.save(createMockTask("PAY-257", "Security Handshake Handler", 8, "In Progress", john, sprint12));
            taskRepository.save(createMockTask("PAY-258", "Rate Limiter Setup", 5, "In Progress", john, sprint12));

            // 12 Done tasks (Total 12 Done)
            for (int i = 1; i <= 12; i++) {
                taskRepository.save(createMockTask("PAY-2" + (20 + i), "Completed Task Module " + i, 3, "Done", john, sprint12));
            }
        }

        // 7. Seed Mock Kafka Events
        if (kafkaEventRepository.count() == 0) {
            kafkaEventRepository.save(KafkaEvent.builder()
                    .taskKey("PAY-247")
                    .eventType("TASK_CREATED")
                    .payload("Task PAY-247 was created and assigned to John (Developer).")
                    .timestamp(LocalDateTime.now().minusHours(4))
                    .build());
            kafkaEventRepository.save(KafkaEvent.builder()
                    .taskKey("PAY-247")
                    .eventType("TASK_MOVED")
                    .payload("Task PAY-247 status was changed to IN_PROGRESS.")
                    .timestamp(LocalDateTime.now().minusHours(3))
                    .build());
            kafkaEventRepository.save(KafkaEvent.builder()
                    .taskKey("PAY-247")
                    .eventType("TASK_BLOCKED")
                    .payload("Task PAY-247 was flagged as BLOCKED. Reason: DB migration pending. Blocker Status: Escalated.")
                    .timestamp(LocalDateTime.now().minusHours(2))
                    .build());
            kafkaEventRepository.save(KafkaEvent.builder()
                    .taskKey("PAY-247")
                    .eventType("KAFKA_PUBLISH")
                    .payload("Kafka: Task event published | Assignee notified.")
                    .timestamp(LocalDateTime.now().minusHours(1))
                    .build());
        }
    }

    private Task createMockTask(String key, String title, int points, String status, User assignee, Sprint sprint) {
        return Task.builder()
                .taskKey(key)
                .title(title)
                .description("Automated SDLC task item details.")
                .points(points)
                .status(status)
                .assignee(assignee)
                .sprint(sprint)
                .isBlocked(false)
                .blockerReason("")
                .blockerStatus("None")
                .build();
    }

    private Team getOrCreateTeam(String name, String description) {
        return teamRepository.findByName(name).orElseGet(() -> 
            teamRepository.save(Team.builder().name(name).description(description).build())
        );
    }

    private User getOrCreateUser(String username, String email, String password, Role role, Team team) {
        return userRepository.findByEmail(email).orElseGet(() -> 
            userRepository.save(User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .team(team)
                    .build())
        );
    }

    private Project getOrCreateProject(String name, String status, Team team, String description) {
        return projectRepository.findByName(name).orElseGet(() -> 
            projectRepository.save(Project.builder()
                    .name(name)
                    .status(status)
                    .team(team)
                    .description(description)
                    .build())
        );
    }

    private Sprint getOrCreateSprint(String name, Integer tasks, Integer points, String status, Project project, String goal, Integer daysLeft) {
        return sprintRepository.findByProjectId(project.getId()).stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .map(s -> {
                    s.setGoal(goal);
                    s.setDaysLeft(daysLeft);
                    return sprintRepository.save(s);
                })
                .orElseGet(() -> 
                    sprintRepository.save(Sprint.builder()
                            .name(name)
                            .tasksCount(tasks)
                            .points(points)
                            .status(status)
                            .project(project)
                            .goal(goal)
                            .daysLeft(daysLeft)
                            .build())
                );
    }

    private Milestone getOrCreateMilestone(String name, LocalDate dueDate, String status, Project project) {
        return milestoneRepository.findByProjectId(project.getId()).stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseGet(() -> 
                    milestoneRepository.save(Milestone.builder()
                            .name(name)
                            .dueDate(dueDate)
                            .status(status)
                            .project(project)
                            .build())
                );
    }
}
