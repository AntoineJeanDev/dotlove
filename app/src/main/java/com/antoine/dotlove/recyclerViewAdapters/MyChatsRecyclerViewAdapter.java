package com.antoine.dotlove.recyclerViewAdapters;

import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoine.dotlove.R;
import com.antoine.dotlove.fragments.ChatsFragment.OnListFragmentInteractionListener;
import com.antoine.dotlove.models.Chat;
import com.antoine.dotlove.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyChatsRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Chat> mChats;
    private final OnListFragmentInteractionListener mListener;
    private final String mUserGender;

    private FirebaseFirestore db;

    public MyChatsRecyclerViewAdapter(FirebaseFirestore firestore, ArrayList<Chat> items, OnListFragmentInteractionListener listener, String userGender) {
        db = firestore;
        mChats = items;
        mListener = listener;
        mUserGender = userGender;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mChats.get(position);

        // TARGET NAME
        String targetName = holder.mItem.getFemaleDisplayName();
        if (mUserGender.equals("female")) {
            targetName = holder.mItem.getMaleDisplayName();
        }
        holder.mTargetName.setText(targetName);

        // LAST MESSAGE
        String lastMessageContent = "Last Message :";
        int size = holder.mItem.getMessages().size();
        if (size > 0) {
            Message lastMessage = holder.mItem.getMessage(size - 1);

            String author;
            if (lastMessage.getUser().equals(holder.mItem.getMaleUser())) {
                author = "You";
            } else {
                author = holder.mItem.getFemaleDisplayName();
            }

            lastMessageContent = author + ": " + lastMessage.getContent();
        }
        holder.mLastMessage.setText(lastMessageContent);

        // photo
        String targetId = holder.mItem.getMaleUser();
        if (mUserGender.equals("male")) {
            targetId = holder.mItem.getFemaleUser();
        }

        db.collection("users").document(targetId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                String photoUrl = task.getResult().get("photoUrl").toString();
                                Picasso.get()
                                    .load(Uri.parse(photoUrl))
                                    .into(holder.mTargetPhoto);
                            }
                        }
                    }
                });

        // OPEN CHAT BUTTON
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Open oneChat Activity
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        // delete chat button
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference chatsRef = db.collection("chats");
                chatsRef.document(mChats.get(position).getId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mChats.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTargetName;
        public final TextView mLastMessage;
        public final ImageView mTargetPhoto;
        public final Button mDeleteButton;

        public Chat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTargetName = (TextView) view.findViewById(R.id.targetName);
            mLastMessage = (TextView) view.findViewById(R.id.chat_last_message);
            mTargetPhoto = (ImageView) view.findViewById(R.id.targetPhoto);
            mDeleteButton = (Button) view.findViewById(R.id.deleteChatButton);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTargetName.getText() + "'";
        }
    }
}
