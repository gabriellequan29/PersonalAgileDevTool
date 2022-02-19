package com.example.agiledevtool.web;

import com.example.agiledevtool.domain.ProjectTask;
import com.example.agiledevtool.services.MapValidationErrorService;
import com.example.agiledevtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{backlog_id}")
    public ResponseEntity<?> addToBacklog(@Valid @RequestBody ProjectTask projectTask,
                                          BindingResult result,
                                          @PathVariable String backlog_id,
                                          Principal principal) {
        ResponseEntity<?> erorrMap = mapValidationErrorService.MapValidationService(result);
        if (erorrMap != null) return erorrMap;

        ProjectTask projectTask1 = projectTaskService.addProjectTask(backlog_id, projectTask, principal.getName());

        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.CREATED);
     }

     @GetMapping("/{backlog_id}")
     public Iterable<ProjectTask> getProjectBacklog(@PathVariable String backlog_id, Principal principal) {
        return projectTaskService.findBacklogById(backlog_id, principal.getName());
     }

     @GetMapping("/{backlog_id}/{pt_id}")
     public ResponseEntity<?> getProjectTask(@PathVariable String backlog_id, @PathVariable String pt_id, Principal principal) {
        ProjectTask projectTask = projectTaskService.findPTByProjectSequence(backlog_id, pt_id, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
     }

    @PatchMapping("/{backlog_id}/{pt_id}")
     public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask,
                                                BindingResult result,
                                                @PathVariable String backlog_id,
                                                @PathVariable String pt_id,
                                                Principal principal) {
         ResponseEntity<?> erorrMap = mapValidationErrorService.MapValidationService(result);
         if (erorrMap != null) return erorrMap;

         ProjectTask updatedTask = projectTaskService.updateByProjectSequence(projectTask, backlog_id, pt_id, principal.getName());

         return new ResponseEntity<ProjectTask>(updatedTask, HttpStatus.OK);
     }

     @DeleteMapping("/{backlog_id}/{pt_id}")
     public ResponseEntity<?> deleteProjectTask(@PathVariable String backlog_id, @PathVariable String pt_id, Principal principal) {
        projectTaskService.deletePTByProjectSequence(backlog_id, pt_id, principal.getName());

        return new ResponseEntity<String>("Project Task "+pt_id+" was deleted successfully", HttpStatus.OK);
     }

}
