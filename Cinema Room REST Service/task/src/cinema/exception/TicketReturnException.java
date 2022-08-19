package cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TicketReturnException extends RuntimeException {

    public TicketReturnException(String cause) {
        super(cause);
    }
}

