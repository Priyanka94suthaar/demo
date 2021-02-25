package app;

import datagenerator.PaymentDataGenerator;
import model.Application;
import model.ApplicationEventsCount;
import model.PaymentData;
import model.Status;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.shaded.guava18.com.google.common.collect.Maps;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Map;

public class PaymentByWindow {

    private static Map<String, Integer> applicationCountMap = Maps.newHashMap();
    static {
        applicationCountMap.put(Application.CHANNEL.name(), 0);
        applicationCountMap.put(Application.ORDER_MANAGER.name(), 0);
        applicationCountMap.put(Application.PAYMENT_ENGINE.name(), 0);
        applicationCountMap.put(Application.AMS.name(), 0);
    }
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        DataStream<PaymentData> payments = env.addSource(new PaymentDataGenerator());
        payments.writeAsText("F://Priyanka/18-Feb/PaymentByWindowInput.txt");
        DataStream<PaymentData> paymentFiltered  = payments.filter(new FilterFunction<PaymentData>() {
            @Override
            public boolean filter(PaymentData value) throws Exception {
                return value.getStatus().equals(Status.STARTED.name());
            }
        });
        paymentFiltered.print();
        paymentFiltered.timeWindowAll(Time.seconds(4)).process(new ProcessAllWindowFunction<PaymentData, ApplicationEventsCount, TimeWindow>() {

            @Override
            public void process(Context context, Iterable<PaymentData> iterable, Collector<ApplicationEventsCount> collector) throws Exception {

                iterable.forEach(paymentData -> {
                    applicationCountMap.put(paymentData.getApplication(),
                            applicationCountMap.get(paymentData.getApplication()) + 1);
                });

                int totalCount = applicationCountMap.values().stream().reduce(Integer::sum).get();
                ApplicationEventsCount ze = ApplicationEventsCount.builder()
                        .channelCount(applicationCountMap.get(Application.CHANNEL.name()))
                        .orderManagerCount(applicationCountMap.get(Application.ORDER_MANAGER.name()))
                        .paymentEngineCount(applicationCountMap.get(Application.PAYMENT_ENGINE.name()))
                        .amsCount(applicationCountMap.get(Application.AMS.name()))
                        .totalCount(totalCount)
                        .build();
                collector.collect(ze);
            }
            }).print();
        //}).writeAsText("F://Priyanka/18-Feb/PaymentByWindowOutput.txt");
        env.execute();
    }
}

