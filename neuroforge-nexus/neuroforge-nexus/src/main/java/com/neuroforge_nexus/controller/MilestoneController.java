package com.neuroforge_nexus.controller;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/milestones")
public class MilestoneController {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public MilestoneController(MilestoneRepository milestoneRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listMilestones(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("currentUser", user);

        List<Milestone> milestones = milestoneRepository.findAll();
        List<Project> projects = projectRepository.findAll();

        model.addAttribute("milestones", milestones);
        model.addAttribute("projects", projects);
        model.addAttribute("newMilestone", new Milestone());

        return "milestones";
    }

    @PostMapping("/create")
    public String createMilestone(@ModelAttribute("newMilestone") Milestone milestone, @RequestParam("projectId") Long projectId,
                                  @RequestParam("dueDateStr") String dueDateStr, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/milestones?error=unauthorized";
        }

        projectRepository.findById(projectId).ifPresent(milestone::setProject);
        milestone.setDueDate(LocalDate.parse(dueDateStr));
        milestoneRepository.save(milestone);

        return "redirect:/milestones?success=milestone_created";
    }
}
