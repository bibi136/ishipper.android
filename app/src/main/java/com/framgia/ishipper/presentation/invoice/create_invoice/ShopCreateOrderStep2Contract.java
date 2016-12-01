package com.framgia.ishipper.presentation.invoice.create_invoice;

/**
 * Created by framgia on 18/11/2016.
 */

public interface ShopCreateOrderStep2Contract {
    interface View {

        void validateNameFailure();

        void validateWeightFailure();

        void validateOrderPriceFailure();

        void validateShipPriceFailure();

        void validateTimeFailure();

        void showTextTime(String stringTime);
    }

    interface Presenter {
        boolean validateDataInput(String name, String weight, String orderPrice, String shipPrice, String time);

        void saveInvoice(String name, String weight, String orderPrice, String shipPrice,
                         String time, String customerName, String customerPhone, String note);

        void pickTime(String userTime);
    }
}
