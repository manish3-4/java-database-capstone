package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final Service service;

    public AdminController(Service service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, ?>> adminLogin(
            @RequestBody Login login) {

        return (ResponseEntity<Map<String, ?>>) service.validateAdmin(login);
    }
}