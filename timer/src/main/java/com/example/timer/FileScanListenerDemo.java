package com.example.timer;

import org.quartz.jobs.FileScanListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileScanListenerDemo implements FileScanListener{

    private static final Logger LOG = LoggerFactory.getLogger(FileScanListenerDemo.class);
    public static final String LISTENER_NAME ="fileScanListener";
    @Override
    public void fileUpdated(String fileName) {
        LOG.info("File update to {}", fileName);
    }

    public String getListenerName(){
        return LISTENER_NAME;
    }
}
