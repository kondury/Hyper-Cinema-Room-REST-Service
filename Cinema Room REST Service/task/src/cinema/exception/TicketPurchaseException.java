package cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TicketPurchaseException extends RuntimeException {

    public TicketPurchaseException(String cause) {
        super(cause);
    }
}
