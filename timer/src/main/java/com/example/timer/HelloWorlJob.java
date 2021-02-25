package com.example.timer;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Component
public class HelloWorlJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorlJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //LOG.info("Hello World");
        JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
        TimerInfo info = (TimerInfo)map.get(HelloWorlJob.class.getSimpleName());
        List l = readFileInList("C://Users/hemant/Downloads/Output.txt");
        info.setCallbackData(l);
        List lget = info.getCallbackData();
        LOG.info(String.valueOf(lget));
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
