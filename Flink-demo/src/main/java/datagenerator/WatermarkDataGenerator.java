package datagenerator;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.util.XORShiftRandom;
import scala.util.parsing.json.JSONObject;

import java.util.Random;

public class WatermarkDataGenerator extends RichParallelSourceFunction<JSONObject> {
    private Random random;

    @Override
    public void run(SourceContext<JSONObject> sourceContext) throws Exception {
        random = new XORShiftRandom(getIterationRuntimeContext().getIndexOfThisSubtask());
        long time =0;
        while(true){

        }
    }

    @Override
    public void cancel() {

    }
}
