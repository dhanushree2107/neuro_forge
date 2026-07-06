package com.neuroforge_nexus.controller;

import com.neuroforge_nexus.model.*;
import com.neuroforge_nexus.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamController(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listTeams(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("currentUser", user);

        List<Team> teams = teamRepository.findAll();
        List<User> users = userRepository.findAll();

        model.addAttribute("teams", teams);
        model.addAttribute("users", users);
        model.addAttribute("newTeam", new Team());

        return "teams";
    }

    @PostMapping("/create")
    public String createTeam(@ModelAttribute("newTeam") Team team, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/teams?error=unauthorized";
        }

        teamRepository.save(team);
        return "redirect:/teams?success=team_created";
    }

    @PostMapping("/assign-member")
    public String assignMember(@RequestParam("userId") Long userId, @RequestParam("teamId") Long teamId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.PROJECT_MANAGER)) {
            return "redirect:/teams?error=unauthorized";
        }

        User targetUser = userRepository.findById(userId).orElse(null);
        Team targetTeam = teamRepository.findById(teamId).orElse(null);

        if (targetUser != null && targetTeam != null) {
            targetUser.setTeam(targetTeam);
            userRepository.save(targetUser);
        }

        return "redirect:/teams?success=member_assigned";
    }
}
