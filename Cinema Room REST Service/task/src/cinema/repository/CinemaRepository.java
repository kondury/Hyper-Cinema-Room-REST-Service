package cinema.repository;

import cinema.model.dto.PricedSeat;
import cinema.model.dto.Seat;
import cinema.model.dto.Ticket;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class CinemaRepository {
    @Value("${cinema.return.error.wrong.token}")
    private String WRONG_TOKEN_ERROR_MESSAGE;
    @Value("${cinema.statistics_password}")
    private String PASSWORD;
    @Value("${cinema.auditorium.total.rows}")
    @Getter
    private int totalRows;
    @Value("${cinema.auditorium.total.columns}")
    @Getter
    private int totalColumns;

    private Map<Seat, Ticket> purchasedSeats;
    private Map<UUID, Ticket> ticketsByToken;
    private Set<PricedSeat> availableSeats;

    private final ToIntFunction<Seat> priceCalculator;

    @Autowired
    public CinemaRepository(@Lazy ToIntFunction<Seat> priceCalculator) {
        this.priceCalculator = priceCalculator;
    }

    public Collection<PricedSeat> getAvailableSeats() {
        return Collections.unmodifiableCollection(availableSeats);
    }

    @PostConstruct
    void init() {
        purchasedSeats = new HashMap<>();
        ticketsByToken = new HashMap<>();
        availableSeats = IntStream.rangeClosed(1, totalRows).boxed()
                .flatMap(row -> IntStream
                        .rangeClosed(1, totalColumns).boxed()
                        .map(column -> new Seat(row, column)))
                .map(seat -> new PricedSeat(seat, priceCalculator.applyAsInt(seat)))
                .collect(Collectors.toSet());
    }

    public boolean isBooked(Seat seat) {
        return purchasedSeats.containsKey(seat);
    }

    public boolean isValidRow(int row) {
        return 1 <= row && row <= getTotalRows();
    }

    public boolean isValidColumn(int column) {
        return 1 <= column && column <= getTotalColumns();
    }

    public boolean addTicket(Ticket ticket) {
        if (isBooked(ticket.getPricedSeat())) {
            return false;
        }
        Seat seat = ticket.getPricedSeat();
        availableSeats.remove(seat);
        purchasedSeats.put(seat, ticket);
        ticketsByToken.put(ticket.getToken(), ticket);
        return true;
    }

    public Ticket removeTicketByToken(UUID token) {
        if (!ticketsByToken.containsKey(token)) {
            throw new NoSuchElementException(WRONG_TOKEN_ERROR_MESSAGE);
        }
        Ticket ticket = ticketsByToken.get(token);
        purchasedSeats.remove(ticket.getPricedSeat());
        availableSeats.add(ticket.getPricedSeat());
        ticketsByToken.remove(token);
        return ticket;
    }

    public boolean checkPassword(String password) {
        return this.PASSWORD.equals(password);
    }

    public Collection<Ticket> getTickets() {
        return purchasedSeats.values();
    }

}
