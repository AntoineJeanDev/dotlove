package com.antoine.dotlove.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.dotlove.activities.LoginActivity;
import com.antoine.dotlove.R;
import com.antoine.dotlove.activities.MainActivity;
import com.antoine.dotlove.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseUser fbUser;
    private User connectedUser;
    FirebaseFirestore db;

    private ArrayList<User> mTargets = new ArrayList<>();
    private int targetIndex = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_main_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button createChatButton = getView().findViewById(R.id.createChatButton);
        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("chat");
            }
        });

        Button createFemaleUserButton = getView().findViewById(R.id.createFemaleUserButton);
        createFemaleUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("user");
            }
        });

        Button validTargetButton = getView().findViewById(R.id.validTargetButton);
        validTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetIndex < mTargets.size() - 1) {
                    mListener.onValidButtonPressed(mTargets.get(targetIndex));
                    targetIndex++;
                } else {
                    Toast.makeText(getContext(), "Plus de target à afficher", Toast.LENGTH_SHORT).show();
                }
                fillTarget(getView());
            }
        });

        Button refuseTargeButton = getView().findViewById(R.id.refuseTargetButton);
        refuseTargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetIndex < mTargets.size() - 1) {
                    targetIndex++;
                } else {
                    Toast.makeText(getContext(), "Plus de target à afficher", Toast.LENGTH_SHORT).show();
                }
                fillTarget(getView());
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String choice) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(choice);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHomeFragmentInteraction(String choice);
        void onValidButtonPressed(User target);
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
                            getTargets();
                        } else {
                            Toast.makeText(getContext(), "no user data found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void fillTarget(View view) {
        User target = mTargets.get(targetIndex);

        if (view != null) {
            // picture
            ImageView photoView = view.findViewById(R.id.home_photo_view);
            Uri url = Uri.parse(target.getPhotoUrl());
            Picasso.get()
                    .load(url)
                    .into(photoView);

            // name
            TextView nameView = view.findViewById(R.id.home_name_view);
            nameView.setText(target.getDisplayName());

            // techLanguages
            TextView techLangView = view.findViewById(R.id.home_techlang_view);
            String langages = "Langages : " + target.getTechLanguages().toString();
            techLangView.setText(langages);
        }
    }

    private void getTargets() {
        CollectionReference usersRef = db.collection("users");

        String genderToSearchFor = "male";
        if (connectedUser.getGender().equals("male")) {
            genderToSearchFor = "female";
        }

        Query query = usersRef.whereEqualTo("gender", genderToSearchFor);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult() != null) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User u = document.toObject(User.class);

                            mTargets.add(u);
                        }

                        fillTarget(getView());
                    } else {
                        Log.w("FIREBASE_TASK", "onComplete: No results", new Throwable());
                    }

                } else {
                    Log.w("FIREBASE_TASK", "onComplete: Query failed", new Throwable());
                }
            }
        });
    }
}
