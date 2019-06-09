package com.antoine.dotlove.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.antoine.dotlove.fragments.ChatsFragment;
import com.antoine.dotlove.fragments.HomeFragment;
import com.antoine.dotlove.R;
import com.antoine.dotlove.models.Chat;
import com.antoine.dotlove.models.Message;
import com.antoine.dotlove.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, ChatsFragment.OnListFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    // profile
    private Button logoutButton;
    BottomNavigationView navView;

    // user
    private FirebaseUser fbUser;
    private User connectedUser;
    FirebaseFirestore db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_profile:

                    ProfileFragment profileFragment = ProfileFragment.newInstance(null, null);
                    openFragment(profileFragment);

                    return true;
                case R.id.navigation_home:

                    HomeFragment homeFragment = HomeFragment.newInstance(null, null);
                    openFragment(homeFragment);

                    return true;
                case R.id.navigation_chats:

                    ChatsFragment chatsFragment = ChatsFragment.newInstance(1);
                    openFragment(chatsFragment);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        db = initFireStore();

        fbUser = initFirebaseAuth();

        if (fbUser != null) {
            CheckUserData();
            navView.setSelectedItemId(R.id.navigation_home);
        } else {
            Intent notLoggedIntent = new Intent(this, LoginActivity.class);
            startActivity(notLoggedIntent);
        }
    }

    public static FirebaseUser initFirebaseAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser();
        } else {
            return null;
        }
    }

    public static FirebaseFirestore initFireStore() {
        return FirebaseFirestore.getInstance();
    }

    private void CheckUserData() {

            db.collection("users").document(fbUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                // if user exists
                                connectedUser = task.getResult().toObject(User.class);

                            } else {
                                // if new user
                                ArrayList<String> techLanguages = new ArrayList<String>();
                                techLanguages.add("PHP");
                                techLanguages.add("JS");
                                techLanguages.add("C++");

                                User u = new User(
                                        fbUser.getDisplayName(),
                                        fbUser.getPhotoUrl().toString(),
                                        "male",
                                        fbUser.getUid(),
                                        techLanguages
                                );

                                db.collection("users")
                                        .document(fbUser.getUid())
                                        .set(u)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this, "Created user data", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "cant create user data", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    });
    }

    public void createChat() {
        Chat c = new Chat(
                new Date(System.currentTimeMillis()),
                fbUser.getUid(),
                UUID.randomUUID().toString(),
                new ArrayList<Message>()
        );

        CollectionReference chatsRef = db.collection("chats");

        chatsRef.add(c)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("FIREBASE_TASK", "onComplete: Chat created");
                        } else {
                            Log.d("FIREBASE_TASK", "onComplete: Chat creation failed");
                        }
                    }
                });
    }

    public void createFemaleUser() {
        ArrayList<String> techLanguages = new ArrayList<String>();
        techLanguages.add("PHP");
        techLanguages.add("JS");
        techLanguages.add("C++");

        User u = new User(
                "Alexandra " + Math.round(Math.random() * 100),
                "https://s3.r29static.com//bin/entry/01c/0,0,2000,2400/x/1692607/image.gif",
                "female",
                UUID.randomUUID().toString(),
                techLanguages
        );

        CollectionReference usersRef = db.collection("users");

        usersRef.document(u.getUid())
                .set(u)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FIREBASE_TASK", "onComplete: User created");
                        } else {
                            Log.d("FIREBASE_TASK", "onComplete: User creation failed");
                        }
                    }
                });
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onProfileFragmentInteraction(String message){
    }

    @Override
    public void onFragmentInteraction(String choice) {
        switch (choice) {
            case "user":
                createFemaleUser();
                break;
            case "chat":
                createChat();
                break;
            default:
        }
    }

    @Override
    public void onListFragmentInteraction(Chat chat) {
        Toast.makeText(MainActivity.this, chat.getCreatedAt().toString(), Toast.LENGTH_SHORT).show();
    }
}
