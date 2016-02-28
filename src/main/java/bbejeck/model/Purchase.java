package bbejeck.model;

import java.util.Date;

/**
 * User: Bill Bejeck
 * Date: 2/20/16
 * Time: 9:09 AM
 */
public class Purchase {

      private String firstName;
      private String lastName;
      private String creditCardNumber;
      private String itemPurchased;
      int quantity;
      double price;
      private Date purchaseDate;
      private String zipCode;

    private Purchase(Builder builder) {
        firstName = builder.firstName;
        lastName = builder.lastName;
        creditCardNumber = builder.creditCardNumber;
        itemPurchased = builder.itemPurchased;
        quantity = builder.quanity;
        price = builder.price;
        purchaseDate = builder.purchaseDate;
        zipCode = builder.zipCode;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Purchase copy) {
        Builder builder = new Builder();
        builder.firstName = copy.firstName;
        builder.lastName = copy.lastName;
        builder.creditCardNumber = copy.creditCardNumber;
        builder.itemPurchased = copy.itemPurchased;
        builder.quanity = copy.quantity;
        builder.price = copy.price;
        builder.purchaseDate = copy.purchaseDate;
        builder.zipCode = copy.zipCode;
        return builder;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getItemPurchased() {
        return itemPurchased;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String getZipCode() {
        return zipCode;
    }


    public static final class Builder {
        private String firstName;
        private String lastName;
        private String creditCardNumber;
        private String itemPurchased;
        private int quanity;
        private double price;
        private Date purchaseDate;
        private String zipCode;

        private Builder() {
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder creditCardNumber(String val) {
            creditCardNumber = val;
            return this;
        }

        public Builder itemPurchased(String val) {
            itemPurchased = val;
            return this;
        }

        public Builder quanity(int val) {
            quanity = val;
            return this;
        }

        public Builder price(double val) {
            price = val;
            return this;
        }

        public Builder purchaseDate(Date val) {
            purchaseDate = val;
            return this;
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Purchase build() {
            return new Purchase(this);
        }
    }
}
