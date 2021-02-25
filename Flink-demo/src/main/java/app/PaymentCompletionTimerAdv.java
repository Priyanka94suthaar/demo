package app;

import datagenerator.CompletionDataGenerator;
import model.PaymentDataCase2;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class PaymentCompletionTimerAdv {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(4);
        DataStream<PaymentDataCase2> payments = env.addSource(new CompletionDataGenerator());
        payments.print();
        payments.writeAsText("F://Priyanka/18-Feb/PaymentCompleteTimeAdvInput.txt",org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        DataStream<Tuple2<String, Long>> completionInterval = payments
                    .keyBy(event -> event.getComponentId())
                    .process(new IntervalProcess());
            DataStream<Tuple2<String, Long>> finalInterval =
                    completionInterval.filter(new FilterFunction<Tuple2<String, Long>>() {
                        @Override
                        public boolean filter(Tuple2<String, Long> value) throws Exception {
                            if("No-Alerts".equals(value.f0)){
                                return false;
                            }else{
                                System.out.println("\n!! Match Alert Received : Payment Id "
                                        + value.f0 + " Time to Complete is "
                                        + value.f1 + " ms" + "\n");
                                return true;
                            }
                        }
                    });
        DataStream<Tuple2<Tuple2<String, Long>, String>>  newStream = finalInterval.flatMap(new TokenizerNewClass());


        finalInterval.print();
        finalInterval.writeAsText("F://Priyanka/18-Feb/PaymentCompleteTimeAdvOutput.txt",org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        newStream.writeAsText("F://Priyanka/18-Feb/PaymentCompleteTimeAlertAdvOutput.txt",org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        env.execute();
        }

    }
final class TokenizerNewClass
        implements FlatMapFunction<Tuple2<String,Long>, Tuple2<Tuple2<String,Long>,String>> {


    @Override
    public void flatMap(Tuple2<String, Long> value, Collector<Tuple2<Tuple2<String, Long>, String>> collector) throws Exception {
        collector.collect(new Tuple2<>(value, "\n                              !! Match Alert Received for Payment Id "
                + value.f0 + "                                       Time to complete this payment transaction is "
                + value.f1 + " ms" + "\n"));


    }
}




