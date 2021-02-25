import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class PaymentTimeDuration extends KeyedProcessFunction<String, Tuple2<String,String>, Tuple2<String,Long>> {
    ValueState<Long> startTime;

    @Override
    public void open(Configuration config){
        startTime = getRuntimeContext().getState(new ValueStateDescriptor<Long>("startTime",Long.class));
    }


    @Override
    public void processElement(Tuple2<String, String> in, Context context, Collector<Tuple2<String, Long>> out) throws Exception {
        switch(in.f1){
            case "START":
                //set the start time when we receives a start event
                startTime.update(context.timestamp());
                //register a timer in 1 hour from start event
                context.timerService().registerEventTimeTimer(context.timestamp()+1*60*60*1000);
                break;
            case "END":
                //emit the duration between start and end time
                long sTime = startTime.value();
                    out.collect(Tuple2.of(in.f0,context.timestamp()-sTime));
                    //clear the state
                    startTime.clear();
            default:
                //do nothing
        }
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<String, Long>> out) throws Exception {
        startTime.clear();
    }
}
