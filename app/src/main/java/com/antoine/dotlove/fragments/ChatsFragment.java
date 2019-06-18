package com.antoine.dotlove.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.antoine.dotlove.activities.LoginActivity;
import com.antoine.dotlove.recyclerViewAdapters.MyChatsRecyclerViewAdapter;
import com.antoine.dotlove.R;
import com.antoine.dotlove.activities.MainActivity;
import com.antoine.dotlove.models.Chat;
import com.antoine.dotlove.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_USER_GENDER = "user-gender";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyChatsRecyclerViewAdapter mAdapter;

    private FirebaseUser fbUser;
    private User connectedUser;
    FirebaseFirestore db;

    // messages
    ArrayList<Chat> myChats = new ArrayList<>();
    private String mUserGender;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatsFragment newInstance(int columnCount, String userGender) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_USER_GENDER, userGender);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mUserGender = getArguments().getString(ARG_USER_GENDER);
        }

        // set user data
        db = MainActivity.initFireStore();

        fbUser = MainActivity.initFirebaseAuth();

        if (fbUser != null) {
            getUserData();
        } else {
            Intent notLoggedIntent = new Intent(getContext(), LoginActivity.class);
            startActivity(notLoggedIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyChatsRecyclerViewAdapter(db, myChats, mListener, mUserGender);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Chat chat);
    }

    // CUSTOM METHODS
    private void getUserData() {
        db.collection("users").document(fbUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            connectedUser =  task.getResult().toObject(User.class);
                            getChats();
                        } else {
                            Toast.makeText(getContext(), "no user data found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getChats() {

        String genderField = "maleUser";
        if (connectedUser.getGender().equals("female")) {
            genderField = "femaleUser";
        }

        CollectionReference chatsRef = db.collection("chats");

        final Query chatsQuery = chatsRef.whereEqualTo(genderField, fbUser.getUid());

        /*chatsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                        Chat c = document.toObject(Chat.class);

                        myChats.add(c);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });*/

        chatsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Chat c = document.toObject(Chat.class);

                        myChats.add(c);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "query failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
