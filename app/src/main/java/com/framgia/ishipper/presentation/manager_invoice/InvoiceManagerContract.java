package com.framgia.ishipper.presentation.manager_invoice;
import com.framgia.ishipper.model.Invoice;

/**
 * Created by vuduychuong1994 on 11/23/16.
 */

public interface InvoiceManagerContract {
    interface View {

        void showReviewDialog(Invoice invoice);

        void removeInvoice(int invoiceId);

        void showInvoiceOnScreen(int statusCodeShipping, int invoiceId);

        void notifyChangeTab(final int type);

        void notifyChangeTab(final int type, final boolean isShowNewInvoice, final int invoiceId);
    }

    interface Presenter {

        void actionTakeInvoice(Invoice invoice);

        void actionShippedInvoice(Invoice invoice);

        void actionFinishInvoice(Invoice invoice);

        void startDetailInvoiceActivity(Invoice invoice);

        void startListShipperRegActivity(Invoice invoice);
    }
}
