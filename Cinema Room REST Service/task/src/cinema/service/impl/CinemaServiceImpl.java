package cinema.service.impl;

import cinema.model.dto.*;
import cinema.repository.CinemaRepository;
import cinema.service.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Service
public class CinemaServiceImpl implements CinemaService {
    @Value("${cinema.purchase.error.row.or.column.out.of.bounds}")
    private String INVALID_ROW_OR_COLUMN_ERROR_MESSAGE;
    @Value("${cinema.purchase.error.ticket.already.purchased}")
    private String TICKET_ALREADY_PURCHASED_ERROR_MESSAGE;
    @SuppressWarnings("SpellCheckingInspection")
    @Value("${cinema.ticket.highprice}")
    private int HIGH_PRICE;
    @SuppressWarnings("SpellCheckingInspection")
    @Value("${cinema.ticket.lowprice}")
    private int LOW_PRICE;
    @SuppressWarnings("SpellCheckingInspection")
    @Value("${cinema.ticket.highpricemaxrow}")
    private int HIGH_PRICE_MAX_ROW;

    private final CinemaRepository cinemaRepository;

    @Autowired
    public CinemaServiceImpl(@Lazy CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }

    @Override
    public SeatsResponse getAvailableSeats() {
        return new SeatsResponse(
                cinemaRepository.getTotalRows(),
                cinemaRepository.getTotalColumns(),
                cinemaRepository.getAvailableSeats()
        );
    }

    @Override
    public Ticket buyTicket(Seat seat) {
        if (cinemaRepository.isBooked(seat)) {
            throw new IllegalStateException(TICKET_ALREADY_PURCHASED_ERROR_MESSAGE);
        }
        int price = calculatePrice(seat);
        PricedSeat pricedSeat = new PricedSeat(seat, price);
        Ticket ticket = new Ticket(pricedSeat);
        if (cinemaRepository.addTicket(ticket)) {
            return ticket;
        } else {
            return null;
        }
    }

    @Override
    public Ticket returnTicket(UUID token) {
        return cinemaRepository.removeTicketByToken(token);
    }

    @Override
    public boolean authorize(String password) {
        return cinemaRepository.checkPassword(password);
    }

    @Override
    public StatsResponse retrieveStatistics() {
        var tickets = cinemaRepository.getTickets();
        int ticketsNumber = tickets.size();
        int availableSeatsNumber = cinemaRepository.getAvailableSeats().size();
        return new StatsResponse(getIncome(tickets), availableSeatsNumber, ticketsNumber);
    }

    private int calculatePrice(Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();
        if (!cinemaRepository.isValidRow(row) || !cinemaRepository.isValidColumn(column)) {
            throw new IndexOutOfBoundsException(INVALID_ROW_OR_COLUMN_ERROR_MESSAGE);
        }
        return seat.getRow() <= HIGH_PRICE_MAX_ROW ? HIGH_PRICE : LOW_PRICE;
    }

    @Bean
    @Override
    public ToIntFunction<Seat> priceCalculator() {
        return this::calculatePrice;
    }

    private int getIncome(Collection<Ticket> tickets) {
        return tickets.stream()
                .mapToInt(ticket -> ticket.getPricedSeat().getPrice())
                .sum();
    }
}
