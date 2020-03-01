package com.android.rupali.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

// use shared preferences
public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    boolean isUserSignedIn = false;
    String email, name;

    private static final int RC_SIGN_IN = 34297;
    private static final String MY_TAG = "FirebaseTag";
    private static final String LOGIN_SHARED_PREF_NAME = "LoginSharedPreferences";
    private static final String EMAIL_KEY = "userEmail";
    private static final String NAME_KEY = "userName";
    private static final String IS_LOGGED_IN_KEY = "isUserLoggedIn";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        checkAuth();
    }

    private void initUI() {
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(LOGIN_SHARED_PREF_NAME,MODE_PRIVATE);

    }

    private void checkAuth() {
        if(sharedPreferences != null){
            isUserSignedIn = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY,false);
        }
        if(isUserSignedIn || auth.getCurrentUser() != null){
            isUserSignedIn =  true;
            startMainActivity();
        }else{
            isUserSignedIn = false;
            createSignInIntent();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void createSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Log.d(MY_TAG,"Sign in success, uid: "+user.getUid()+" email: "+user.getEmail());
                    isUserSignedIn = true;
                    saveUserInSharedPreferences(user);
                    startMainActivity();
                }

            } else {
                if (response != null) {
                    Log.d(MY_TAG,"Sign in error: "+response.getError());
                }
            }
        }
    }

    private void saveUserInSharedPreferences(FirebaseUser user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_KEY, user.getDisplayName());
        editor.putString(EMAIL_KEY, user.getEmail());
        editor.putBoolean(IS_LOGGED_IN_KEY, isUserSignedIn);
        editor.apply();
    }

    private void deleteUserFromSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(IS_LOGGED_IN_KEY);
        editor.remove(NAME_KEY);
        editor.remove(EMAIL_KEY);
        editor.apply();
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(MY_TAG,"Sign out complete ");
                        isUserSignedIn = false;
                        deleteUserFromSharedPreferences();
                    }
                });
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(MY_TAG,"delete user complete ");
                        isUserSignedIn = false;
                        deleteUserFromSharedPreferences();
                    }
                });
    }

}
