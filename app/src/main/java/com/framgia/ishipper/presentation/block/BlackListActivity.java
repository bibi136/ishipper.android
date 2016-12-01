package com.framgia.ishipper.presentation.block;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.widget.dialog.ConfirmDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by vuduychuong1994 on 9/30/16.
 */

public class BlackListActivity extends BaseToolbarActivity implements BlackListContract.View {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private BlackListAdapter mBlackListAdapter;
    private List<User> mBlackListUser;
    private User mCurrentUser;
    private BlackListPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        mPresenter = new BlackListPresenter(this, this);
        mPresenter.getBlackList(mCurrentUser);
    }

    @Override
    public void initViews() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBlackListUser = new ArrayList<>();
        mBlackListAdapter = new BlackListAdapter(BlackListActivity.this, mBlackListUser);
        mRecyclerView.setAdapter(mBlackListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_favorite_add:
                mPresenter.startSearchUserActivity();
                return true;
            case R.id.menu_delete_all:
                if (mBlackListUser == null || mBlackListUser.size() == 0)
                    return false;
                showConfirmDialog();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == Const.RequestCode.REQUEST_SEARCH_BLACKLIST) {
            final User user = data.getParcelableExtra(Const.KEY_USER);
            if (user == null) return;
            mPresenter.addUserToBlackList(mCurrentUser, user);
        }
    }

    @Override
    public void showConfirmDialog() {
        new ConfirmDialog(BlackListActivity.this).setIcon(
                R.drawable.ic_delete_white_24dp).setMessage(
                getString(R.string.delete_all_dialog_message)).setTitle(
                getString(R.string.delete_all_dialog_title)).setButtonCallback(
                new ConfirmDialog.ConfirmDialogCallback() {
                    @Override
                    public void onPositiveButtonClick(final ConfirmDialog confirmDialog) {
                        mPresenter.deleteAllBlackList(mCurrentUser);
                        confirmDialog.cancel();
                    }

                    @Override
                    public void onNegativeButtonClick(ConfirmDialog confirmDialog) {
                        confirmDialog.cancel();
                    }
                }).show();
    }

    @Override
    public void showListUser(ListUserData listUser) {
        mBlackListAdapter.getUserList().clear();
        if (listUser != null) {
            mBlackListAdapter.getUserList().addAll(listUser.getShippersList());
        }
        mBlackListAdapter.notifyDataSetChanged();
    }

    @Override
    public void insertUser(int index, User blockUser) {
        mBlackListUser.add(index, blockUser);
        mBlackListAdapter.notifyDataSetChanged();
        mRecyclerView.getLayoutManager().scrollToPosition(Const.ZERO);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.title_black_list_shipper;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_blacklist_user;
    }
}
