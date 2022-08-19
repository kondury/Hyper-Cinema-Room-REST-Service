package cinema.service;

import cinema.model.dto.*;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.function.ToIntFunction;

public interface CinemaService {

    SeatsResponse getAvailableSeats();
    Ticket buyTicket(Seat seat);

    Ticket returnTicket(UUID token);

    boolean authorize(String password);

    StatsResponse retrieveStatistics();

    @Bean
    ToIntFunction<Seat> priceCalculator();

}
