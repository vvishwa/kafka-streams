package bbejeck.processor.purchases;

import bbejeck.model.Purchase;
import bbejeck.model.RewardAccumulator;
import org.apache.kafka.streams.processor.AbstractProcessor;

/**
 * User: Bill Bejeck
 * Date: 2/20/16
 * Time: 9:44 AM
 */
public class CustomerRewards extends AbstractProcessor<String,Purchase> {

    @Override
    public void process(String key, Purchase value) {
        String customer = value.getLastName()+", "+value.getFirstName();
        double amount = value.getPrice() * value.getQuantity();
        RewardAccumulator accumulator = new RewardAccumulator(customer,amount);
        context().forward(key,accumulator);
        context().commit();

    }
}
