package model;

import lombok.Builder;
import lombok.Getter;

import static Utils.SerializerProvider.GSON;

@Builder
@Getter
public class ApplicationEventsCount {

    private int channelCount;
    private int orderManagerCount;
    private int paymentEngineCount;
    private int amsCount;
    private int totalCount;

    public String toString() {
        return GSON.toJson(this);
    }
}
