package bbejeck.model;

/**
 * User: Bill Bejeck
 * Date: 2/20/16
 * Time: 9:55 AM
 */
public class RewardAccumulator {

    private String custmerName;
    private double purchaseTotal;

    public RewardAccumulator(String custmerName, double purchaseTotal) {
        this.custmerName = custmerName;
        this.purchaseTotal = purchaseTotal;
    }

    public String getCustmerName() {
        return custmerName;
    }

    public double getPurchaseTotal() {
        return purchaseTotal;
    }
}
