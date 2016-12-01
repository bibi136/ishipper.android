package com.framgia.ishipper.presentation.favorite;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.AddFavoriteListData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.presentation.profile.SearchUserActivity;
import com.framgia.ishipper.widget.dialog.ConfirmDialog;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavoriteListFragment extends BaseFragment implements FavoriteListContract.View {
    private static final String TAG = "FavoriteListFragment";

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    private Context mContext;
    private FavoriteListAdapter mFavoriteListAdapter;
    private List<User> mFavoriteList = new ArrayList<>();
    private User mCurrentUser;
    private FavoriteListPresenter mPresenter;


    public FavoriteListFragment() {
    }

    public static FavoriteListFragment newInstance() {
        return new FavoriteListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
        mPresenter = new FavoriteListPresenter(mContext, this);
        mPresenter.getFavoriteList(mCurrentUser);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_blacklist_shipper;
    }

    @Override
    public void initViews() {
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mFavoriteListAdapter = new FavoriteListAdapter(mContext, mFavoriteList);
        mRecyclerView.setAdapter(mFavoriteListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_user_manage, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite_add:
                mPresenter.startSearchUserActivity();
                return true;
            case R.id.menu_delete_all:
                if (mFavoriteList == null ||  mFavoriteList.size() == 0) return false;
                showConfirmDialog();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Const.RequestCode.REQUEST_SEARCH_FAVORITE) {
                final User user = data.getParcelableExtra(Const.KEY_USER);
                if (user != null) {
                    mPresenter.addFavoriteUser(mCurrentUser, user);
                }
            }
        }
    }

    @Override
    public void showConfirmDialog() {
        new ConfirmDialog(mContext)
                .setIcon(R.drawable.ic_delete_white_24dp)
                .setMessage(getString(R.string.delete_all_dialog_message))
                .setTitle(getString(R.string.delete_all_dialog_title))
                .setButtonCallback(new ConfirmDialog.ConfirmDialogCallback() {
                    @Override
                    public void onPositiveButtonClick(ConfirmDialog confirmDialog) {
                        mPresenter.deleteAllFavoriteList(mCurrentUser);
                        confirmDialog.cancel();
                    }

                    @Override
                    public void onNegativeButtonClick(ConfirmDialog confirmDialog) {
                        confirmDialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void showListUser(ListUserData listUserData) {
        mFavoriteList.clear();
        if (listUserData != null) {
            mFavoriteList.addAll(listUserData.getShippersList());
        }
        mFavoriteListAdapter.notifyDataSetChanged();
    }

    @Override
    public void insertUser(int index, User favoriteUser) {
        mFavoriteList.add(Const.ZERO, favoriteUser);
        mFavoriteListAdapter.notifyDataSetChanged();
    }
}
