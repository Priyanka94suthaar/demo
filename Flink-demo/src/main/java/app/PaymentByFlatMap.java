package app;

import datagenerator.PaymentDataGenerator;
import model.PaymentData;
import model.Status;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class PaymentByFlatMap {
    public static void main(String[] args) throws Exception {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(1);
            DataStream<PaymentData> payments = env.addSource(new PaymentDataGenerator());
            payments.writeAsText("F://Priyanka/18-Feb/PaymentByFlatMapInput.txt");
            DataStream<PaymentData> paymentFiltered  = payments.filter(new FilterFunction<PaymentData>() {
                @Override
                public boolean filter(PaymentData value) throws Exception {
                    return value.getStatus().equals(Status.STARTED.name());
                }
            }).keyBy(PaymentData::getComponentId);
           paymentFiltered.print();
            DataStream<Tuple2<String, Integer>> counts = paymentFiltered
                    .map(new ApplicationMapper())
                    .keyBy(0)
                    .sum(1);
            counts.print();
            counts.writeAsText("F://Priyanka/18-Feb/PaymentByFlatMapOutput.txt");
            env.execute();
    }
}

final class ApplicationMapper implements MapFunction<PaymentData, Tuple2<String, Integer>>{

    @Override
    public Tuple2<String, Integer> map(PaymentData paymentData) throws Exception {
        return new Tuple2<String, Integer>(paymentData.getApplication(), 1);
    }
}
