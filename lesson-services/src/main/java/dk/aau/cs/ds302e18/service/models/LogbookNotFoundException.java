package dk.aau.cs.ds302e18.service.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LogbookNotFoundException extends RuntimeException {
    public LogbookNotFoundException(String s) {
        super(s);
    }
}
