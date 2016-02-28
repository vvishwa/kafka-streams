package bbejeck.processor.purchases;

import bbejeck.model.Purchase;
import bbejeck.model.PurchasePattern;
import org.apache.kafka.streams.processor.AbstractProcessor;

/**
 * User: Bill Bejeck
 * Date: 2/20/16
 * Time: 9:39 AM
 */
public class PurchasePatterns extends AbstractProcessor<String, Purchase> {

    @Override
    public void process(String key, Purchase value) {
        PurchasePattern purchasePattern = PurchasePattern.newBuilder().date(value.getPurchaseDate())
                .item(value.getItemPurchased())
                .zipCode(value.getZipCode()).build();
        context().forward(key, purchasePattern);
        context().commit();
    }
}
