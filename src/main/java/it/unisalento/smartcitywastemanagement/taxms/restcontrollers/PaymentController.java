package it.unisalento.smartcitywastemanagement.taxms.restcontrollers;


import com.stripe.model.Charge;
import it.unisalento.smartcitywastemanagement.taxms.domain.PaymentInfo;
import it.unisalento.smartcitywastemanagement.taxms.dto.PaymentRequestDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.PaymentResponseDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.ResponseDTO;
import it.unisalento.smartcitywastemanagement.taxms.mappers.PaymentInfoMapper;
import it.unisalento.smartcitywastemanagement.taxms.service.PayTaxService;
import it.unisalento.smartcitywastemanagement.taxms.service.StripePaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/tax/pay")
public class PaymentController {

    @Autowired
    private PayTaxService payTaxService;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @PreAuthorize("hasRole('ROLE_Citizen')")
    @RequestMapping(value="/{taxID}", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> payATax(@PathVariable("taxID") String taxID ,@RequestBody @Valid PaymentRequestDTO paymentRequestDTO) throws Exception{

        PaymentInfo paymentInfo = paymentInfoMapper.toPaymentInfo(paymentRequestDTO);

        Charge charge = payTaxService.payTax(paymentInfo, taxID);

        if(charge.getStatus().equals("succeeded")) {

            return new ResponseEntity<ResponseDTO>(
                    new ResponseDTO("Payment done",
                            new PaymentResponseDTO(charge)),
                    HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(
                    new ResponseDTO("Payment failed",
                            charge.getOutcome().getReason()),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
