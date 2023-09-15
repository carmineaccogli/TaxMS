package it.unisalento.smartcitywastemanagement.taxms.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TaxAlreadyPaidException extends Exception{
}
