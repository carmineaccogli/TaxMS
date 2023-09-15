package it.unisalento.smartcitywastemanagement.taxms.mappers;


import it.unisalento.smartcitywastemanagement.taxms.domain.PaymentInfo;
import it.unisalento.smartcitywastemanagement.taxms.dto.PaymentRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentInfoMapper {

    public PaymentInfo toPaymentInfo(PaymentRequestDTO paymentRequestDTO) {
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setEmail(paymentRequestDTO.getEmail());
        paymentInfo.setAmount(paymentRequestDTO.getAmount());
        paymentInfo.setFullName(paymentRequestDTO.getFullName());
        paymentInfo.setCardToken(paymentRequestDTO.getCardToken());

        return paymentInfo;
    }
}
