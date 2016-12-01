package com.framgia.ishipper.presentation.profile;

import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.ReviewUser;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListReviewData;

import java.util.List;

/**
 * Created by HungNT on 11/22/16.
 */

class UserReviewPresenter implements UserReviewContract.Presenter {
    private final UserReviewContract.View mView;
    private BaseFragment mBaseFragment;

    public UserReviewPresenter(UserReviewContract.View view, BaseFragment fragment) {
        mView = view;
        mBaseFragment = fragment;
    }

    @Override
    public void getListReview(String userInvoiceId) {
        User currentUser = Config.getInstance().getUserInfo(mBaseFragment.getContext());
        API.getListReviews(
                currentUser.getAuthenticationToken(),
                userInvoiceId,
                new API.APICallback<APIResponse<ListReviewData>>() {
                    @Override
                    public void onResponse(APIResponse<ListReviewData> response) {
                        List<ReviewUser> reviewUsers = response.getData().getReviewUsers();
                        mView.notifyListReviewerChange(reviewUsers);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mBaseFragment.showUserMessage(message);
                    }
                });
    }
}
