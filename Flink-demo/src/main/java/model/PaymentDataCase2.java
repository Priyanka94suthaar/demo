package model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.Objects;

import static Utils.SerializerProvider.GSON;

@Getter
@Builder
public class PaymentDataCase2 {
    private String application;
    private Long componentId;
    private String status;
    private Long eventTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentDataCase2)) return false;
        PaymentDataCase2 that = (PaymentDataCase2) o;
        return componentId == that.componentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId);
    }

    public String toString() {
        return GSON.toJson(this);
    }
}
