package com.antoine.dotlove.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.antoine.dotlove.data.FemaleData;
import com.antoine.dotlove.fragments.ChatsFragment;
import com.antoine.dotlove.fragments.GenderDialog;
import com.antoine.dotlove.fragments.HomeFragment;
import com.antoine.dotlove.R;
import com.antoine.dotlove.fragments.ProfileFragment;
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

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements
        HomeFragment.OnFragmentInteractionListener,
        ChatsFragment.OnListFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        GenderDialog.NoticeDialogListener {

    // user
    private FirebaseUser fbUser;
    private User connectedUser;
    FirebaseFirestore db;

    // for user input
    private String tempGender = "";

    // oneChat
    public static final int RC_CHAT_OPENED = 489;

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

                    ChatsFragment chatsFragment = ChatsFragment.newInstance(1, connectedUser.getGender());
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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (isConnected() && hasFireStore()) {
            CheckUserData();
            navView.setSelectedItemId(R.id.navigation_home);
        } else {
            Intent notLoggedIntent = new Intent(this, LoginActivity.class);
            startActivity(notLoggedIntent);
        }

    }

    public void openGenderDialog() {
        DialogFragment genderDialog = new GenderDialog();
        genderDialog.setCancelable(false);
        genderDialog.show(getSupportFragmentManager(), "choose-gender");
    }

    public static FirebaseUser initFirebaseAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser();
        } else {
            return null;
        }
    }

    public boolean isConnected() {
        fbUser = initFirebaseAuth();
        return fbUser != null;
    }

    public static FirebaseFirestore initFireStore() {
        return FirebaseFirestore.getInstance();
    }

    public boolean hasFireStore() {
        db = initFireStore();
        return true;
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
                                createNewUserData("gender");
                            }
                        }
                    });
    }

    private void createNewUserData(String stage) {

        User tempU = new User();
        tempU.setDisplayName(fbUser.getDisplayName());
        tempU.setPhotoUrl(fbUser.getPhotoUrl().toString());
        tempU.setUid(fbUser.getUid());
        tempU.setGender(tempGender);

        ArrayList<String> techLanguages = new ArrayList<String>();
        techLanguages.add("PHP");
        techLanguages.add("JS");
        techLanguages.add("C++");

        tempU.setTechLanguages(techLanguages);

        switch (stage) {
            case "gender":

                openGenderDialog();

                break;

            case "persist":

                db.collection("users")
                        .document(fbUser.getUid())
                        .set(tempU)
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

                break;

            default:
                throw new RuntimeException("Wrong stage in createNewUserData");
        }


    }

    public void createChat(User target) {
        Chat c = new Chat();
        c.setCreatedAt(new Date(System.currentTimeMillis()));
        c.setMessages(new ArrayList<Message>());

        if (connectedUser.getGender().equals("male")) {
        c.setMaleDisplayName(fbUser.getDisplayName());
        c.setMaleUser(fbUser.getUid());
        } else {
            c.setFemaleDisplayName(fbUser.getDisplayName());
            c.setFemaleUser(fbUser.getUid());
        }


        if (target == null) {
            c.setFemaleUser(UUID.randomUUID().toString());
            c.setFemaleDisplayName(FemaleData.getRandomGirlName());

        } else {
            if (connectedUser.getGender().equals("male")) {
            c.setFemaleUser(target.getUid());
            c.setFemaleDisplayName(target.getDisplayName());
            } else {
                c.setMaleUser(target.getUid());
                c.setMaleDisplayName(target.getDisplayName());
            }
        }

        CollectionReference chatsRef = db.collection("chats");
        DocumentReference newChatRef = chatsRef.document();

        c.setId(newChatRef.getId());

        newChatRef.set(c)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (!task.isSuccessful()) {
                           Toast.makeText(MainActivity.this, "Error creating chat", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
    }

    public void createFemaleUser() {
        ArrayList<String> techLanguages = new ArrayList<>();
        techLanguages.add("PHP");
        techLanguages.add("JS");
        techLanguages.add("C++");

        User u = new User(
                FemaleData.getRandomGirlName(),
                FemaleData.getRandomGirlPicture(),
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
    public void onHomeFragmentInteraction(String choice) {
        switch (choice) {
            case "user":
                createFemaleUser();
                break;
            case "chat":
                createChat(null);
                break;
            default:
        }
    }

    @Override
    public void onValidButtonPressed(User target) {
        createChat(target);
    }

    @Override
    public void onListFragmentInteraction(Chat chat) {
        Intent openChatIntent = new Intent(this, OneChat.class);
        Bundle bundle = new Bundle();
        bundle.putString(OneChat.CHAT_ID, chat.getId());
        bundle.putString(OneChat.USER_GENDER, connectedUser.getGender());
        openChatIntent.putExtras(bundle);
        startActivityForResult(openChatIntent, RC_CHAT_OPENED);
    }

    @Override
    public void onGenderChose(String gender) {
        tempGender = gender;
        createNewUserData("persist");
    }

}
