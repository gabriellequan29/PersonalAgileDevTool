package com.example.agiledevtool.services;

import com.example.agiledevtool.domain.Backlog;
import com.example.agiledevtool.domain.Project;
import com.example.agiledevtool.domain.User;
import com.example.agiledevtool.exceptions.ProjectIdException;
import com.example.agiledevtool.exceptions.ProjectNotFoundException;
import com.example.agiledevtool.repositories.BacklogRepository;
import com.example.agiledevtool.repositories.ProjectRepository;
import com.example.agiledevtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    // create a new project or update an existed project
    public Project saveOrUpdateProject(Project project, String username){


        try{
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            // create a new project
            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            // update an existed project
            if(project.getId() != null) {
                Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
                if(existingProject != null && (!existingProject.getProjectLeader().equals(username))){
                    throw new ProjectNotFoundException("Project not found in your account");
                }else if(existingProject == null){
                    throw new ProjectNotFoundException("Project with ID: '"+project.getProjectIdentifier()+"' cannot be updated because it doesn't exist");
                } else {
                    project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
                }
            }

            return projectRepository.save(project);

        }catch (Exception exception) {
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
        }
    }

    // find a project by identifier
    public Project findProjectByIdentifier(String projectId, String username){
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project==null){
            throw new ProjectIdException("Project ID'" +projectId+"' does not exists");
        }

        if(!project.getProjectLeader().equals(username)){
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return project;
    }

    // find all projects
    public Iterable<Project> findAllProjects(String username){

        return projectRepository.findAllByProjectLeader(username);
    }

    // delete by identifier
    public void deleteProjectByIdentifier(String projectId, String username){
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        projectRepository.delete(findProjectByIdentifier(projectId, username));
    }

}

