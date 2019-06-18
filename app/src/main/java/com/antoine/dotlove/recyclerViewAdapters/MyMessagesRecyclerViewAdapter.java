package com.antoine.dotlove.recyclerViewAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoine.dotlove.R;
import com.antoine.dotlove.models.Chat;
import com.antoine.dotlove.models.Message;



public class MyMessagesRecyclerViewAdapter extends RecyclerView.Adapter<MyMessagesRecyclerViewAdapter.ViewHolder> {

    private final Chat mChat;

    private final Context mContext;

    public MyMessagesRecyclerViewAdapter(Chat chat, Context context) {
        mChat = chat;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_one_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mChat.getMessage(position);

        holder.mContent.setText(holder.mItem.getContent());

        String author;
        if (holder.mItem.getUser().equals(mChat.getMaleUser())) {
            author = mChat.getMaleDisplayName();
            holder.mContent.setBackgroundColor(mContext.getColor(R.color.user_blue));
        } else {
            author = mChat.getFemaleDisplayName();
            holder.mContent.setBackgroundColor(mContext.getColor(R.color.user_pink));
        }
        holder.mAuthor.setText(author);
    }

    @Override
    public int getItemCount() {
        return mChat.getMessages().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContent;
        public final TextView mAuthor;

        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContent = (TextView) view.findViewById(R.id.message_content);
            mAuthor = (TextView) view.findViewById(R.id.message_author);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContent.getText() + "'";
        }
    }

}
