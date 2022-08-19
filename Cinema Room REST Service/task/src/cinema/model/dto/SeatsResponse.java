package cinema.model.dto;

import lombok.NonNull;
import java.util.Collection;

@SuppressWarnings("unused")
public record SeatsResponse(int totalRows, int totalColumns, @NonNull Collection<PricedSeat> availableSeats) {
}

