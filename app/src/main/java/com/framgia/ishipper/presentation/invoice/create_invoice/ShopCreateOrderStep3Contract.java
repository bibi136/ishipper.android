package com.framgia.ishipper.presentation.invoice.create_invoice;

import com.framgia.ishipper.model.Invoice;

/**
 * Created by framgia on 18/11/2016.
 */

public interface ShopCreateOrderStep3Contract {
    interface View {

    }

    interface Presenter {
        void requestCreateInvoice(Invoice newInvoice);
        void startRouteActivity();
    }
}
