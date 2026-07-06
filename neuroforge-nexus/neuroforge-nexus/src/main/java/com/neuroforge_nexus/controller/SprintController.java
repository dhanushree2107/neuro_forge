package com.neuroforge_nexus.controller;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/sprints")
public class SprintController {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public SprintController(SprintRepository sprintRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listSprints(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("currentUser", user);

        List<Sprint> sprints = sprintRepository.findAll();
        List<Project> projects = projectRepository.findAll();

        model.addAttribute("sprints", sprints);
        model.addAttribute("projects", projects);
        model.addAttribute("newSprint", new Sprint());

        return "sprint";
    }

    @PostMapping("/create")
    public String createSprint(@ModelAttribute("newSprint") Sprint sprint, @RequestParam("projectId") Long projectId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/sprints?error=unauthorized";
        }

        projectRepository.findById(projectId).ifPresent(sprint::setProject);
        sprintRepository.save(sprint);

        return "redirect:/sprints?success=sprint_created";
    }
}
