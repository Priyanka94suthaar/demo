package datagenerator;

import helper.DataGeneratorHelper;
import model.EventCharges;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.time.Instant;

public class EventChargeGenerator implements SourceFunction<EventCharges> {
    private volatile boolean running = true;

    @Override
    public void run(SourceContext<EventCharges> ctx) throws Exception {
        long id = 1;
        int count=0;
        while (count<10) {
            //EventCharges fare = new EventCharges(id);
            EventCharges eventCharges = EventCharges.builder()
                    .componentId(id)
                    .appName(DataGeneratorHelper.getRandomAppName().name())
                    .status(DataGeneratorHelper.getRandomEndStatus().name())
                    .endTime(DataGeneratorHelper.generateTime())
                    .build();

            id += 1;
            ctx.collectWithTimestamp(eventCharges, Instant.now().toEpochMilli());
            ctx.emitWatermark(new Watermark(Instant.now().toEpochMilli()));

            // match our event production rate to that of the TaxiRideGenerator
            Thread.sleep(PaymentDataGenerator.SLEEP_MILLIS_PER_EVENT);
        }
    }

    @Override
    public void cancel() {
        running=false;
    }
}
