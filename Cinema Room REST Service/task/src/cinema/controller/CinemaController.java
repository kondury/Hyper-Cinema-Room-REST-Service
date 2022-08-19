package cinema.controller;

import cinema.exception.TicketPurchaseException;
import cinema.exception.TicketReturnException;
import cinema.model.dto.*;
import cinema.service.CinemaService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
public class CinemaController {

    private final CinemaService cinemaService;
    @Value("${cinema.statistics.error.wrong.password}")
    private String WRONG_PASSWORD_ERROR_MESSAGE;

    @Autowired
    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @GetMapping("/seats")
    public SeatsResponse getAvailableSeats() {
        return cinemaService.getAvailableSeats();
    }

    @PostMapping("/purchase")
    public Ticket buyTicket(@RequestBody Seat seat) {
        try {
            return cinemaService.buyTicket(seat);
        } catch (IndexOutOfBoundsException | IllegalStateException e) {
            throw new TicketPurchaseException(e.getMessage());
        }
    }

    @PostMapping("/return")
    public Map<String, PricedSeat> returnTicket(@RequestBody Map<String, UUID> tokenJson) {
        try {
            Ticket ticket = cinemaService.returnTicket(tokenJson.get("token"));
            return Map.of("returned_ticket", ticket.getPricedSeat());
        } catch (NoSuchElementException e) {
            throw new TicketReturnException(e.getMessage());
        }
    }

    @PostMapping("/stats")
    public StatsResponse retrieveStatistics(@RequestParam Optional<String> password) throws AuthenticationException {
        if (password.isEmpty() || !cinemaService.authorize(password.get())) {
            throw new AuthenticationException(WRONG_PASSWORD_ERROR_MESSAGE);
        }
        return cinemaService.retrieveStatistics();
    }

    @ExceptionHandler(value = { TicketPurchaseException.class, TicketReturnException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse exceptionHandler(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse exceptionHandler(AuthenticationException e) {
        return new ErrorResponse(e.getMessage());
    }
}
