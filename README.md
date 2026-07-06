# NeuroForge Nexus

NeuroForge Nexus is an enterprise-grade platform that manages the complete Software Development Lifecycle (SDLC) from requirements gathering to deployment, monitoring, and release management.

This repository contains the implementation of **Milestone 1: Project & User Management**.

---

## 🚀 Milestone 1 Features
- **User Registration & Login**: Interactive forms with Spring Security authentication.
- **Role-Based Access Control (RBAC)**: Distinct permissions for `Admin`, `Project Manager`, `Developer`, `Tester`, and `DevOps Engineer`. Role restricted creation sections are dynamically locked.
- **Team Management**: Custom team creation and developer-to-team assignments.
- **Project Service**: Creation of projects (default project: `FinCore Nexus`).
- **Sprint Planning**: Planning sprints with task counts and story points.
- **Milestone & Release Tracking**: Milestone targeting and delivery tracking.
- **Keycloak IAM Integration Dashboard**: Status indicators and Keycloak role mappings simulated in the UI.

---

## 🛠️ Technology Stack
- **Backend**: Spring Boot 4, Java 25, Spring Security 6
- **Database**: H2 Database (In-Memory default for local developer review)
- **Frontend**: Thymeleaf templates with responsive dark-theme CSS layout

---

## 💻 How to Run the Application

Navigate to the project directory and run the Maven wrapper:

### For PowerShell:
```powershell
cd neuroforge-nexus/neuroforge-nexus
.\mvnw.cmd spring-boot:run
```

### For Command Prompt:
```cmd
cd neuroforge-nexus/neuroforge-nexus
mvnw.cmd spring-boot:run
```

Open your browser and navigate to: **[http://localhost:8082](http://localhost:8082)**

---

## 🔑 Default Seeded Accounts
- **Admin**: `admin@neuroforge.com` / `admin123`
- **Project Manager**: `pm@neuroforge.com` / `pm123`
- **Developer**: `dev@neuroforge.com` / `dev123`
- **Tester**: `tester@neuroforge.com` / `tester123`
- **DevOps**: `devops@neuroforge.com` / `devops123`
