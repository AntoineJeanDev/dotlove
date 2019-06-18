package com.antoine.dotlove.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.dotlove.R;
import com.antoine.dotlove.models.Chat;
import com.antoine.dotlove.models.Message;
import com.antoine.dotlove.recyclerViewAdapters.MyMessagesRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class OneChat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyMessagesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Chat mChat;
    private String userGender;

    private FirebaseFirestore db;

    public static final int RC_BACK = 456;
    public static final String CHAT_ID = "chat_id";
    public static final String USER_GENDER = "user_gender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_chat);

        Intent intent = getIntent();
        if (intent != null) {
            db = FirebaseFirestore.getInstance();

            Bundle bundle = intent.getExtras();

            if (intent.hasExtra(CHAT_ID)) {
                String chatId = bundle.getString(CHAT_ID);
                userGender = bundle.getString(USER_GENDER);
                getChat(chatId);
            }

        }

        Button backButtton = findViewById(R.id.backButton);
        backButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RC_BACK);
                finish();
            }
        });

        Button sendMessageButton = findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageInput = findViewById(R.id.messageInput);
                addMessage(messageInput.getText().toString());
                messageInput.setText("");

                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    public void getChat(String id) {

        CollectionReference chatsRef = db.collection("chats");

        chatsRef.document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            mChat = documentSnapshot.toObject(Chat.class);
                            fillTargetName();
                            initList();
                        }
                    }
                });

       /* chatsRef.document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                mChat = task.getResult().toObject(Chat.class);
                                fillTargetName();
                                initList();
                            }
                        } else {
                            Toast.makeText(OneChat.this, "Error getting chat", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

    }

    public void initList() {
        recyclerView = findViewById(R.id.messages_recycle_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyMessagesRecyclerViewAdapter(mChat,OneChat.this);
        recyclerView.setAdapter(mAdapter);
    }

    public void addMessage(String content) {
        FirebaseUser fb = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference chatRef = db.collection("chats").document(mChat.getId());

        ArrayList<Message> messages = mChat.getMessages();
        messages.add(new Message(fb.getUid(), content));

        chatRef.update("messages", messages);
    }

    public void fillTargetName() {
        String targetNameString = mChat.getFemaleDisplayName();
        if (userGender.equals("female")) {
            targetNameString = mChat.getMaleDisplayName();
        }

        TextView targetName = (TextView) findViewById(R.id.chat_target_name);
        targetName.setText(targetNameString);
    }
}
