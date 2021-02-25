package app;

import datagenerator.CompletionDataGenerator;
import model.PaymentDataCase2;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class PaymentCompletionTimer {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<PaymentDataCase2> payments = env.addSource(new CompletionDataGenerator());
        payments.print();
        payments.writeAsText("F://Priyanka/18-Feb/PaymentCompleteTimeInput.txt",org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        DataStream<Tuple2<String, Long>> completionInterval = payments
                    .keyBy(event -> event.getComponentId())
                    .map(new CompletionIntervalMap())
                    .filter(new FilterNoAlerts());

        DataStream<Tuple2<Tuple2<String, Long>, String>>  newStream = completionInterval.flatMap(new TokenizerOutClass());

            completionInterval.print();
        newStream.writeAsText("F://Priyanka/18-Feb/PaymentCompleteTimeAlertOutput.txt",org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE).setParallelism(1);
        env.execute();
        }
    }

final class StatewithKeyedTimestamp{
    public long key;
    public long startTime;
    public long endTime;
}

final class CompletionIntervalMap extends RichMapFunction<PaymentDataCase2, Tuple2<String, Long>>{
    private transient ValueState<StatewithKeyedTimestamp> keyedTimestamp;

    @Override
    public void open(Configuration parameters) throws Exception {
        keyedTimestamp = getRuntimeContext().getState(new ValueStateDescriptor<>("myState", StatewithKeyedTimestamp.class));
    }

    @Override
    public Tuple2<String, Long> map(PaymentDataCase2 paymentData) throws Exception {
        Tuple2<String,Long> retTuple
                = new Tuple2<String,Long>("No-Alerts",0L);

        StatewithKeyedTimestamp current = keyedTimestamp.value();
        if (current == null) {
            current = new StatewithKeyedTimestamp();
            current.key = paymentData.getComponentId();
        }

        if("STARTED".equals(paymentData.getStatus())){
            if(current.endTime > 0L){
                long timeInterval = current.endTime - paymentData.getEventTime();
                retTuple = new Tuple2<String, Long>(paymentData.getComponentId().toString(), Math.abs(timeInterval));
                keyedTimestamp.clear();
            }
            if(current.startTime == 0L){
                current.startTime = paymentData.getEventTime();
                keyedTimestamp.update(current);
            }
        }
        if("COMPLETED".equals(paymentData.getStatus())){
            if(current.startTime > 0L){
                 long timeInterval = paymentData.getEventTime() - current.startTime;
                 retTuple = new Tuple2<String, Long>(paymentData.getComponentId().toString(), Math.abs(timeInterval));
                keyedTimestamp.clear();
            }
            if(current.endTime == 0L){
                current.endTime = paymentData.getEventTime();
                keyedTimestamp.update(current);
            }


        }
        return retTuple;
    }
}

final class FilterNoAlerts implements FilterFunction<Tuple2<String,Long>> {

    @Override
    public boolean filter(Tuple2<String, Long> alert) throws Exception {
        if(alert.f0.equals("No-Alerts")){
            return false;
        }else{
            System.out.println("\n!! Match Alert Received : Payment Id "
                    + alert.f0 + " Time to Complete is "
                    + alert.f1 + " ms" + "\n");
            return true;
        }
    }
}

final class TokenizerOutClass
        implements FlatMapFunction<Tuple2<String,Long>, Tuple2<Tuple2<String,Long>,String>> {


    @Override
    public void flatMap(Tuple2<String, Long> value, Collector<Tuple2<Tuple2<String, Long>, String>> collector) throws Exception {
        collector.collect(new Tuple2<>(value, "\n!! Match Alert Received : Payment Id "
                + value.f0 + " Time to Complete is "
                + value.f1 + " ms" + "\n"));


    }
}
