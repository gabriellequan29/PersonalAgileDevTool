package com.example.agiledevtool.web;

import com.example.agiledevtool.domain.Project;
import com.example.agiledevtool.services.MapValidationErrorService;
import com.example.agiledevtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    // create a new project
    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal){

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null) return errorMap;

        Project project1 = projectService.saveOrUpdateProject(project, principal.getName());
        return new ResponseEntity<Project>(project1, HttpStatus.CREATED);
    }

    // find a project by identifier
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId,  Principal principal){

        Project project = projectService.findProjectByIdentifier(projectId, principal.getName());

        return new ResponseEntity<Project>(project, HttpStatus.OK);

    }

    // find all projects
    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal){
        return projectService.findAllProjects(principal.getName());
    }

    // delete by identifier
    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProjectById(@PathVariable String projectId, Principal principal){
        projectService.deleteProjectByIdentifier(projectId, principal.getName());

        return new ResponseEntity<String>("Project with ID: '"+projectId+"' was deleted", HttpStatus.OK);
    }
}
