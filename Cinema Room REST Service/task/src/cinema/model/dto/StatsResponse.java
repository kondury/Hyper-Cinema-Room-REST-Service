package cinema.model.dto;

@SuppressWarnings("unused")
public record StatsResponse(int currentIncome, int numberOfAvailableSeats, int numberOfPurchasedTickets) {
}
