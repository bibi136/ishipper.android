package com.framgia.ishipper.presentation.block;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.widget.dialog.ConfirmDialog;
import com.framgia.ishipper.util.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vuduychuong1994 on 9/15/16.
 */
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUserList;


    public BlackListAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_black_list_shipper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.mTextName.setText(user.getName());

    }

    @Override
    public int getItemCount() {
        if (mUserList != null)
            return mUserList.size();

        return 0;
    }

    public List<User> getUserList() {
        return mUserList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUserName) TextView mTextName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btnRemove)
        public void onClick() {
            new ConfirmDialog(mContext)
                    .setTitle(mContext.getString(R.string.dialog_delete_message))
                    .setMessage(mContext.getString(R.string.dialog_remove_user_from_blacklist))
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setButtonCallback(new ConfirmDialog.ConfirmDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(ConfirmDialog confirmDialog) {
                            deleteUserFromList(getAdapterPosition());
                            confirmDialog.cancel();
                        }

                        @Override
                        public void onNegativeButtonClick(ConfirmDialog confirmDialog) {
                            confirmDialog.cancel();
                        }
                    }).show();
        }

        private void deleteUserFromList(final int position) {
            User currentUser = Config.getInstance().getUserInfo(mContext);
            final Dialog loadingDialog = CommonUtils.showLoadingDialog(mContext);
            API.deleteUserBlacklist(
                    currentUser.getAuthenticationToken(),
                    currentUser.getUserType(),
                    mUserList.get(position).getBlackListUserId(),
                    new API.APICallback<APIResponse<EmptyData>>() {
                        @Override
                        public void onResponse(APIResponse<EmptyData> response) {
                            loadingDialog.dismiss();
                            mUserList.remove(position);
                            notifyItemRemoved(position);
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    }
            );
        }
    }
}
