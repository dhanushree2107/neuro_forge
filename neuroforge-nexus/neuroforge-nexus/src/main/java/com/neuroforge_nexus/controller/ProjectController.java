package com.neuroforge_nexus.controller;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository projectRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listProjects(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("currentUser", user);
        
        List<Project> projects = projectRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        
        model.addAttribute("projects", projects);
        model.addAttribute("teams", teams);
        model.addAttribute("newProject", new Project());
        
        return "projects";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute("newProject") Project project, @RequestParam("teamId") Long teamId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/projects?error=unauthorized";
        }
        
        teamRepository.findById(teamId).ifPresent(project::setTeam);
        projectRepository.save(project);
        
        return "redirect:/projects?success=created";
    }
}
