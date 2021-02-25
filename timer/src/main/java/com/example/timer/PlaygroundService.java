package com.example.timer;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class PlaygroundService {
    private final SchedulerService scheduler;

    public PlaygroundService(SchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    public void runHelloWorldJob(){
        TimerInfo info = new TimerInfo();
        info.setTotalFireCount(5);
        info.setRepeatIntervalMs(30000);
        info.setInitialOffsetMs(1000);
        List l = readFileInList("C://Users/hemant/Downloads/Output.txt");

        info.setCallbackData(l);
        List<String> l1=info.getCallbackData();
        //info.setCallbackData("My Callback data");
        scheduler.schedule(HelloWorlJob.class,info);

    }
    public static List<String> readFileInList(String fileName)
    {
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lines;
    }
}
