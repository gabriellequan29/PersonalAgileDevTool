package com.example.agiledevtool.services;

import com.example.agiledevtool.domain.Backlog;
import com.example.agiledevtool.domain.ProjectTask;
import com.example.agiledevtool.exceptions.ProjectNotFoundException;
import com.example.agiledevtool.repositories.BacklogRepository;
import com.example.agiledevtool.repositories.ProjectRepository;
import com.example.agiledevtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    // create new project task
    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {

        try {
            // set Backlog
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();
            projectTask.setBacklog(backlog);

            // get sq# from backlog and increase it
            Integer sq = backlog.getPTSequence();
            sq++;
            backlog.setPTSequence(sq);

            // set projectSequence# and projectIdentifier
            projectTask.setProjectSequence(backlog.getProjectIdentifier()+"-"+sq);
            projectTask.setProjectIdentifier(projectIdentifier);

            // Initialize status
            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }

            if(projectTask.getPriority()==null||projectTask.getPriority()==0) {
                projectTask.setPriority(3);
            }

            return projectTaskRepository.save(projectTask);

        }catch(Exception e) {
            throw new ProjectNotFoundException("Project not Found");
        }
    }

    // get all project tasks in backlog
    public Iterable<ProjectTask> findBacklogById(String id, String username){
       projectService.findProjectByIdentifier(id, username);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    // get project task by project sequence
    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
        //make sure we are searching on the right backlog
        projectService.findProjectByIdentifier(backlog_id, username);

        //make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if(projectTask == null) {
            throw new ProjectNotFoundException("Project Task with ID: '" + pt_id + "' does not exist");
        }

        if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task '" + pt_id + "' does not exist in project: '" + backlog_id);
        }
        return projectTask;
    }

    // update project task by project sequence
    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTask = updatedTask;
        return projectTaskRepository.save(projectTask);
    }

    // delete project task by project sequence
    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTaskRepository.delete(projectTask);

    }

}
