package it.unisalento.smartcitywastemanagement.taxms.service;

import com.stripe.Stripe;
import com.stripe.model.*;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerRetrieveParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class StripePaymentServiceImpl implements StripePaymentService{

    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


    public Customer createCustomer(String token, String email, String fullname) throws Exception {
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", email);
        customerParams.put("source", token);
        customerParams.put("name",fullname);
        return Customer.create(customerParams);
    }
    private Customer getCustomer(String id) throws Exception {
        return Customer.retrieve(id);
    }
    public Charge chargeNewCard(String token, double amount) throws Exception {
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int)(amount * 100));
        chargeParams.put("currency", "EUR");
        chargeParams.put("source", token);
        Charge charge = Charge.create(chargeParams);
        return charge;
    }

    public Charge chargeCustomerCard(String customerId, double amount) throws Exception {
        String sourceCard = getCustomer(customerId).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int)(amount * 100));
        chargeParams.put("currency", "EUR");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", sourceCard);
        Charge charge = Charge.create(chargeParams);
        return charge;
    }

    /** FUNZIONE PER EFFETTUARE IL PAGAMENTO TRAMITE STRIPE
     * 1. Creo l'oggetto di pagamento impostando i campi base
     * 2. Espando le sorgenti di pagamento per il cliente e ottengo le informazioni sul cliente
     * 3. Ottengo altre informazioni aggiuntive dal token che saranno necessarie
     * 4. Itero sulle sorgenti di pagamento
     *      4.1 Se il metodo di pagamento è una card con stessa impronta di quella definita dal token, allora salvo l'id della sorgente
     * 5. Check di questo id
     *      5.1 Se non è nullo, uso tale metodo di pagamento e lo indico nell'oggetto charge
     *      5.2 Altrimenti creo il nuovo metodo di pagamento e lo uso per concludere la transazione
     * 6. Effettuo il pagamento
     **/
    public Charge chargeCustomerCard(String customerId, double amount, String token) throws Exception {


        // 1
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int) (amount * 100));
        chargeParams.put("currency", "EUR");
        chargeParams.put("customer", customerId);

        // 2
        CustomerRetrieveParams params = CustomerRetrieveParams.builder()
                .addExpand("sources").build();
        Customer customer = Customer.retrieve(customerId, params, null);

        // 3
        Card customerCard= Token.retrieve(token).getCard();
        String id = Token.retrieve(token).getId();

        String sourceID =null;

        // 4
        for (PaymentSource source : customer.getSources().getData()) {
            // 4.1
            if (source instanceof Card && ((Card) source).getFingerprint().equals(customerCard.getFingerprint())) {
                sourceID = source.getId();
                break;
            }
        }

        // 5
        if(sourceID != null) {
            // 5.1
            chargeParams.put("source",sourceID);
        }
        else {
            // 5.2
            Map<String, Object> sourceParams = new HashMap<String,Object>();
            sourceParams.put("source",id);

            PaymentSource newSource = customer.getSources().create(sourceParams);

            chargeParams.put("source", newSource.getId());
        }


        // 6
        Charge charge = Charge.create(chargeParams);

        return charge;
    }

    public Customer getCustomerByEmail(String email) throws Exception {

        // Crea un oggetto CustomerListParams per filtrare i clienti per indirizzo email
        CustomerListParams listParams = CustomerListParams.builder()
                .setEmail(email)
                .build();

        // Esegui la query per ottenere i clienti con lo stesso indirizzo email
        Iterable<Customer> customers = Customer.list(listParams).autoPagingIterable();

        // Restituisci il primo cliente corrispondente (se presente)
        for (Customer customer : customers) {
            return customer;
        }

        // Se non viene trovato alcun cliente corrispondente, restituisci null
        return null;
    }

    public Charge chargeCustomerDefaultCard(String customerId, double amount) throws Exception {
        String sourceCard = getCustomer(customerId).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int) (amount * 100));
        chargeParams.put("currency", "EUR");
        chargeParams.put("customer", customerId);
        chargeParams.put("source", sourceCard);
        Charge charge = Charge.create(chargeParams);
        return charge;
    }

}

