package app;

import datagenerator.EventChargeGenerator;
import datagenerator.PaymentDataGenerator;
import model.EventCharges;
import model.PaymentData;
import model.Status;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

public class PaymentConnected {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        DataStream<PaymentData> payments = env.addSource(new PaymentDataGenerator());
        payments.writeAsText("F://Priyanka/18-Feb/PaymentConnectedInput.txt");
        DataStream<PaymentData> paymentFiltered  = payments.filter(new FilterFunction<PaymentData>() {
            @Override
            public boolean filter(PaymentData value) throws Exception {
                return value.getStatus().equals(Status.STARTED.name());
            }
        }).keyBy(PaymentData::getComponentId);

        DataStream<EventCharges> charges = env.addSource(new EventChargeGenerator());
        DataStream<EventCharges> chargesFiltered =charges.filter(
                (FilterFunction<EventCharges>) value -> value.getStatus().equals(Status.COMPLETED.name())
        ).keyBy(EventCharges::getComponentId);
        DataStream<Tuple2<PaymentData, EventCharges>> enrichedStreams = paymentFiltered
                .connect(chargesFiltered)
                .flatMap(new EnrichmentFunction())
                .uid("enrichment");

        enrichedStreams.writeAsText("F://Priyanka/18-Feb/PaymentConnectedOutput.txt",org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE);
        enrichedStreams.print();
        env.execute();
    }

    public static class EnrichmentFunction extends RichCoFlatMapFunction<PaymentData, EventCharges, Tuple2<PaymentData, EventCharges>> {
        // keyed, managed state
        private ValueState<PaymentData> startState;
        private ValueState<EventCharges> endState;

        @Override
        public void open(Configuration config) {
            startState = getRuntimeContext().getState(new ValueStateDescriptor<>("saved ride", PaymentData.class));
            endState = getRuntimeContext().getState(new ValueStateDescriptor<>("saved fare", EventCharges.class));
        }

        @Override
        public void flatMap1(PaymentData paymentData, Collector<Tuple2<PaymentData, EventCharges>> out) throws Exception {
            EventCharges endState = this.endState.value();
            if (endState != null) {
                this.endState.clear();
                out.collect(Tuple2.of(paymentData, endState));
            } else {
                startState.update(paymentData);
            }
        }

        @Override
        public void flatMap2(EventCharges fare, Collector<Tuple2<PaymentData, EventCharges>> out) throws Exception {
            PaymentData ride = startState.value();
            if (ride != null) {
                startState.clear();
                out.collect(Tuple2.of(ride, fare));
            } else {
                endState.update(fare);
            }
        }
    }
}