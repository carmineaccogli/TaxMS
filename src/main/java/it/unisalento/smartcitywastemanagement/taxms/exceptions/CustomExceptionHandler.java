package it.unisalento.smartcitywastemanagement.taxms.exceptions;
import com.stripe.exception.StripeException;
import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.dto.ExceptionDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Set;

/**
 *  Quando una eccezione viene sollevata, questa classe si occupa di restituire nel body
 *  un json contenente le informazioni sull'eccezione.
 *  Informazioni eccezione:
 *      - codice eccezione (fa riferimento ad un elenco di eccezioni già stilato su un documento excel)
 *      - nome eccezione (nome della Classe Java relativa all'eccezione)
 *      - descrizione eccezione
 */
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CitizenNotFoundException.class)
    public ResponseEntity<Object> citizenNotFoundHandler(CitizenNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(
                        7,
                        CitizenNotFoundException.class.getSimpleName(),
                        "Citizen not found"
                ));
    }

    @ExceptionHandler(AnnualTaxAlreadyEmittedException.class)
    public ResponseEntity<Object> annualTaxAlreadyEmittedHandler(AnnualTaxAlreadyEmittedException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionDTO(
                        25,
                        AnnualTaxAlreadyEmittedException.class.getSimpleName(),
                        "Taxes for the current year have already been issued"
                ));
    }

    @ExceptionHandler(TaxRateNotFoundException.class)
    public ResponseEntity<Object> taxRateNotFoundHandler(TaxRateNotFoundException ex) {

        String message = ex.getMessage();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(
                        26,
                        TaxRateNotFoundException.class.getSimpleName(),
                        "Tax Rate not found: "+message
                ));
    }


    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> webClientResponseHandler(WebClientResponseException ex) {

        HttpStatusCode httpStatus = ex.getStatusCode();
        String responseBody = ex.getResponseBodyAsString();

        return ResponseEntity.status(httpStatus)
                .body(new ExceptionDTO(
                        23,
                        WebClientResponseException.class.getSimpleName(),
                        responseBody
                ));
    }

    @ExceptionHandler(TaxNotFoundException.class)
    public ResponseEntity<Object> taxNotFoundHandler(TaxNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(
                        27,
                        TaxNotFoundException.class.getSimpleName(),
                        "Tax with the current ID not found"
                ));
    }

    @ExceptionHandler(TaxAlreadyPaidException.class)
    public ResponseEntity<Object> taxAlreadyPaidHandler(TaxAlreadyPaidException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionDTO(
                        28,
                        TaxAlreadyPaidException.class.getSimpleName(),
                        "A payment for the current tax is already registered"
                ));
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<Object> stripeExceptionHandler(StripeException ex) {

        String message = ex.getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        29,
                        StripeException.class.getSimpleName(),
                        "Error during payment "+message
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationHandler(
            ConstraintViolationException ex) {

        StringBuilder errorString = new StringBuilder();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String message = violation.getMessage();
            String property = violation.getPropertyPath().toString();
            errorString.append(property).append(":").append(message).append(". ");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        30,
                        ConstraintViolationException.class.getSimpleName(),
                        errorString.deleteCharAt(errorString.length() - 2).toString()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumetNotValidHandler(
            MethodArgumentNotValidException ex) {

        StringBuilder errorString = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errorString.append(errorMessage).append(".");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        17,
                        MethodArgumentNotValidException.class.getSimpleName(),
                        errorString.deleteCharAt(errorString.length() - 1).toString()
                ));
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Object> webClientRequestHandler(WebClientRequestException ex) {

        String responseBody = ex.getMessage();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDTO(
                        24,
                        WebClientRequestException.class.getSimpleName(),
                        responseBody
                ));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> noHandlerFoundHandler(NoHandlerFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getBody());
    }











}

