package com.framgia.ishipper.presentation.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 10/21/16.
 */

public class NotificationAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context mContext;
    private List<Notification> mNotificationList;
    private OnItemClickListener mClickListener;

    public NotificationAdapter(Context context, List<Notification> notificationList, OnItemClickListener listener) {
        mContext = context;
        mClickListener = listener;
        mNotificationList = notificationList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bindData();
        } else {
            ((NotificationViewHolder) holder).bindData(mNotificationList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationList == null ? 0 : mNotificationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mNotificationList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_loading_progress) ProgressBar mProgressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindData() {
            mProgressBar.setIndeterminate(true);
        }
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Notification notificationItem;
        User currentUser;
        @BindView(R.id.imgAvatar) ImageView mImgAvatar;
        @BindView(R.id.tvContent) TextView mTvContent;
        @BindView(R.id.tvTimePost) TextView mTvTimePost;
        View mView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            currentUser = Config.getInstance().getUserInfo(mContext);
            mView = itemView;
        }

        private void bindData(final Notification item) {
            mTvContent.setText(item.getContent());
            notificationItem = item;
            mTvTimePost.setText(item.getTimePost());
            if (!item.isRead()) {
                mView.setBackgroundResource(R.color.back_ground_dark_grey);
            }
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onClick(item);

                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mView.getId() == view.getId()) {
                API.updateNotification(currentUser.getUserType(),
                        String.valueOf(notificationItem.getId()),
                        currentUser.getAuthenticationToken(), true,
                        new API.APICallback<APIResponse<EmptyData>>() {
                            @Override
                            public void onResponse(
                                    APIResponse<EmptyData> response) {
                                //TODO: read notificationItem
                            }

                            @Override
                            public void onFailure(int code, String message) {

                            }
                        }
                );

            }
        }
    }

    public interface OnItemClickListener {
        void onClick(Notification notification);
    }
}
