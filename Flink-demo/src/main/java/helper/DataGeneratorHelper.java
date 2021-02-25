package helper;

import Utils.RandomNumberGenerator;
import model.Application;
import model.ApplicationNames;
import model.Status;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataGeneratorHelper {

    private final static List<Status> TERMINAL_STATUS_LIST = Lists.newArrayList(Status.COMPLETED, Status.FAILED);
    private final static List<Status> START_STATUS_LIST = Lists.newArrayList(Status.STARTED, Status.ACKNOWLEDGED, Status.DIRTY);
    private final static List<Status> COMPLETION_STATUS_LIST = Lists.newArrayList(Status.STARTED, Status.COMPLETED, Status.FAILED);

    private final static List<Integer> COMP_ID_LIST = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);

    private final static Random RANDOM = new Random();

    public static ApplicationNames getRandomAppName() {
        ApplicationNames name = ApplicationNames.values()[RANDOM.nextInt(ApplicationNames.values().length)];
        return name;
    }

    public static Status getRandomEndStatus() {
        return TERMINAL_STATUS_LIST.get(RANDOM.nextInt(TERMINAL_STATUS_LIST.size()));
    }

    public static Status getRandomStartStatus() {
        return START_STATUS_LIST.get(RANDOM.nextInt(START_STATUS_LIST.size()));
    }

    public static Status getRandomCompletionStatus(){
        return COMPLETION_STATUS_LIST.get(RANDOM.nextInt(COMPLETION_STATUS_LIST.size()));
    }

    public static int getRandomCompId(){
        return COMP_ID_LIST.get(RANDOM.nextInt(COMP_ID_LIST.size()));
    }

    public static Date generateTime() {
        int offsetInMins = RandomNumberGenerator.getRandomNumber(0, 5);
        return DateUtils.addMinutes(new Date(), offsetInMins);
    }

    public static Application getRandomApplication() {
        return Application.values()[RANDOM.nextInt(Application.values().length)];
    }

}
