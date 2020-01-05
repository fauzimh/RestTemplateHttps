package com.azifu.resttemplatehttps.server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    @GetMapping(path = {"/",""})
    public String hello() {
        return "hello world from " + getClass().getSimpleName();
    }

    @PostMapping(path = "/postMessage")
    public String postMessage(@RequestBody String message) {
        return "your message is" + message;
    }
}
