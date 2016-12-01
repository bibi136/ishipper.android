package com.framgia.ishipper.presentation.profile;

import com.framgia.ishipper.model.ReviewUser;

import java.util.List;

/**
 * Created by HungNT on 11/22/16.
 */

public class UserReviewContract {
    interface View {
        void notifyListReviewerChange(List<ReviewUser> reviewUsers);
    }

    interface Presenter {
        void getListReview(String userInvoiceId);
    }
}
