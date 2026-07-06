package com.neuroforge_nexus.config;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final MilestoneRepository milestoneRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, TeamRepository teamRepository,
                           ProjectRepository projectRepository, SprintRepository sprintRepository,
                           MilestoneRepository milestoneRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.milestoneRepository = milestoneRepository;
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

        // 2. Seed Users
        getOrCreateUser("admin", "admin@neuroforge.com", "admin123", Role.ADMIN, null);
        getOrCreateUser("pm", "pm@neuroforge.com", "pm123", Role.PROJECT_MANAGER, team12);
        getOrCreateUser("dev", "dev@neuroforge.com", "dev123", Role.DEVELOPER, backendTeam);
        getOrCreateUser("tester", "tester@neuroforge.com", "tester123", Role.TESTER, qaTeam);
        getOrCreateUser("devops", "devops@neuroforge.com", "devops123", Role.DEVOPS_ENGINEER, devopsTeam);

        // 3. Seed Projects
        Project finCoreNexus = getOrCreateProject("FinCore Nexus", "Active", team12, "Enterprise banking platform SDLC management system.");

        // 4. Seed Sprints
        getOrCreateSprint("Sprint 12", 23, 67, "Active", finCoreNexus);

        // 5. Seed Milestones
        getOrCreateMilestone("Release 2.3", LocalDate.of(2026, 6, 20), "Active", finCoreNexus);
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

    private Sprint getOrCreateSprint(String name, Integer tasks, Integer points, String status, Project project) {
        return sprintRepository.findByProjectId(project.getId()).stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseGet(() -> 
                    sprintRepository.save(Sprint.builder()
                            .name(name)
                            .tasksCount(tasks)
                            .points(points)
                            .status(status)
                            .project(project)
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
