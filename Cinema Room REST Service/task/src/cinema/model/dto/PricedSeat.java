package cinema.model.dto;

import lombok.Getter;

@Getter
public class PricedSeat extends Seat {

    private final int price;

    public PricedSeat(Seat seat, int price) {
        super(seat.getRow(), seat.getColumn());
        this.price = price;
    }
}
