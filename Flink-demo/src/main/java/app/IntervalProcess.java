package app;

import model.PaymentDataCase2;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class IntervalProcess extends KeyedProcessFunction<Long, PaymentDataCase2, Tuple2<String, Long>> {
    private transient ValueState<KeyedState> state;

    @Override
    public void open(Configuration parameters) throws Exception {
        state = getRuntimeContext().getState(new ValueStateDescriptor<>("myState", KeyedState.class));
    }

    @Override
    public void processElement(PaymentDataCase2 paymentData, Context context, Collector<Tuple2<String, Long>> collector) throws Exception {
        Tuple2<String, Long> retTuple
                = new Tuple2<String, Long>("No-Alerts", 0L);

        KeyedState current = state.value();
        if (current == null) {
            current = new KeyedState();
            current.key = paymentData.getComponentId();
            current.isMatch = false;
        }
     //   System.out.println("context Time " + context.timestamp());
     //   System.out.println("event" + paymentData.getEventTime());

        if ("STARTED".equals(paymentData.getStatus())) {

            if (!current.isMatch) {
                if ( current.endTime != null && current.endTime > 0L) {
                    long timeInterval = current.endTime - context.timestamp();
                    retTuple = new Tuple2<String, Long>(paymentData.getComponentId().toString(), Math.abs(timeInterval));
                    //  state.clear();
                    current.startTime = context.timestamp();
                    current.isMatch = true;
                    state.update(current);

                }
                if (current.startTime == null || current.startTime == 0L) {
                    current.startTime = context.timestamp();
                    state.update(current);
                }
            }

        }
        if ("COMPLETED".equals(paymentData.getStatus())) {

            if (!current.isMatch) {
                if (current.startTime != null && current.startTime > 0L) {
                    long timeInterval = context.timestamp() - current.startTime;
                    retTuple = new Tuple2<String, Long>(paymentData.getComponentId().toString(), Math.abs(timeInterval));

                    current.endTime = context.timestamp();
                    current.isMatch = true;
                    state.update(current);
                }
                if (current.endTime == null || current.endTime == 0L) {
                    current.endTime = context.timestamp();
                    state.update(current);
                }
            }

        }
        //return retTuple;
        collector.collect(retTuple);

    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<String, Long>> out) throws Exception {
        super.onTimer(timestamp, ctx, out);
    }
}
final class KeyedState {
    public Long key;
    public Long startTime;
    public Long endTime;
    public boolean isMatch;
}