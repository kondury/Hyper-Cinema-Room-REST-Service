package cinema.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Ticket {
    private final UUID token = UUID.randomUUID();
    @JsonProperty("ticket")
    @NonNull private final PricedSeat pricedSeat;
}