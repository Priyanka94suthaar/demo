package com.example.timer;

import org.quartz.JobExecutionContext;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/timer")
@EnableScheduling
public class PlaygroundController {
    private PlaygroundService playgroundService;

    public PlaygroundController(PlaygroundService playgroundService) {
       this.playgroundService=playgroundService;
    }

    @PostMapping("/runHello")
    public void runHelloWorldJob(){
        playgroundService.runHelloWorldJob();

    }



}
