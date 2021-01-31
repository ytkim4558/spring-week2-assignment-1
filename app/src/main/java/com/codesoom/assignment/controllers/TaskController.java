package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.JsonTask;
import com.codesoom.assignment.application.TaskApplicationService;
import com.codesoom.assignment.application.TaskJsonTransfer;
import com.codesoom.assignment.domain.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin
public class TaskController {
    TaskApplicationService taskApplicationService = new TaskApplicationService();
    TaskJsonTransfer transfer = new TaskJsonTransfer();

    @GetMapping
    public List<JsonTask> getAllTasks() throws UnknownException {
        return transfer.taskListToJson(taskApplicationService.getAllTasks())
            .orElseThrow(UnknownException::new);
    }

    @GetMapping("/{id}")
    public JsonTask getSpecificTask(@PathVariable Long id) throws TaskNotFoundException {
        return taskApplicationService.findTask(id)
            .flatMap(
                it -> transfer.taskToJson(it)
            ).orElseThrow(
                () -> new TaskNotFoundException(id)
            );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JsonTask createTask(@RequestBody JsonTask jsonTask) throws TaskNotFoundException, UnknownException {
        Long createdTaskId = taskApplicationService.createTask(jsonTask.title);
        Task createdTask = taskApplicationService.findTask(createdTaskId)
            .orElseThrow(
                () -> new TaskNotFoundException(createdTaskId)
            );
        return transfer.taskToJson(createdTask)
            .orElseThrow(UnknownException::new);
    }

    @PutMapping("/{id}")
    public JsonTask updateTitle(@PathVariable Long id, @RequestBody JsonTask jsonTask) throws TaskNotFoundException, UnknownException {
        taskApplicationService.updateTaskTitle(id, jsonTask.title).orElseThrow(
            () -> new TaskNotFoundException(id)
        );
        Task updatedTask = taskApplicationService.findTask(id)
            .orElseThrow(
                () -> new TaskNotFoundException(id)
            );
        return transfer.taskToJson(updatedTask)
                .orElseThrow(UnknownException::new);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) throws TaskNotFoundException {
        taskApplicationService.deleteTask(id).orElseThrow(
            () -> new TaskNotFoundException(id)
        );
    }
}
