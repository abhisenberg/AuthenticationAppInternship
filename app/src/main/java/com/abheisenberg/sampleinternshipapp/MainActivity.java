package com.abheisenberg.sampleinternshipapp;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends BaseActivity implements RegisterFragment.OnRegisterAttemptListener, LoginFragment.OnLoginAttempt,
        WelcomeFragment.OnSignoutAttempt{

    /*
        This app contains only one activity and several fragments to operate faster,
        all the fragments do different works like loggin-in, registering etc.
        Since it is better and cleaner to user more fragments and less activities.
     */
    private static final String TAG = "MainAct";
    private static int BACK_TO_EXIT = 0 ;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        goToFragment(mAuth.getCurrentUser());
    }

    /*
        This function determines the fragment to be shown, if the user is logged in, then show the
        welcome screen, else show the login screen.
     */
    private void goToFragment(FirebaseUser currentUser) {
        if(currentUser == null || !currentUser.isEmailVerified()){
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                    .replace(R.id.Fragment, new LoginFragment())
                    .commit();
        } else {
            Log.d(TAG, "User: "+currentUser.getEmail());
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                    .replace(R.id.Fragment, new WelcomeFragment())
                    .commit();
        }
    }

    /*
        A simple signIn function which implements Firebase's signin method.
     */
       private void signIn(String email, String pw){
        Log.d(TAG, "signIn attempt from "+email);

        showLoadingDialog();

        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            if(!mAuth.getCurrentUser().isEmailVerified()){

                                Snackbar.make(findViewById(R.id.Fragment), getString(R.string.notverified),
                                        Snackbar.LENGTH_LONG)
                                        .show();

                            } else {

                                getFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                        .replace(R.id.Fragment, new WelcomeFragment())
                                        .commit();

                            }

                        } else {
                            //Sign in failed, display an error msg to user
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        hideLoadingDialog();

                    }
                });
    }

    /*
        A simple signout function which implements Firebase's signout method.
     */
    private void signOut(){
        mAuth.signOut();

        Toast.makeText(this, getString(R.string.signedout), Toast.LENGTH_SHORT).show();

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.Fragment, new LoginFragment())
                .commit();
    }

    /*
        Store the user's name in the database, which can be retrieved by entering his email-id.
        This is necessary because firebase doesnt provide any method to store extra info apart from
        email and password to authenticate. Hence it is necessary to create a custom function based
        on database.
     */
    private void createNewUser(String name, String email){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, name);

        mDatabase.child("users").child(email.split("@")[0]).setValue(user);
    }

    /*
        A function which implements firebase's default createUser function, it takes email and pw
        as arguments, since the name is stored separately.
     */
    private void createAccount(String email, String pw){
        Log.d(TAG, "createAccount: "+email);

        showLoadingDialog();

        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            sendEmailVerification();

                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.Fragment, new LoginFragment())
                                    .commit();

                        } else {

                            FirebaseException e = (FirebaseException) task.getException();
                            Log.d(TAG, "CREATION_FAILED: "+e.getMessage());
                             Toast.makeText(MainActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideLoadingDialog();
                    }
                });
    }

    /*
        Sends verification link to the user, uses FIrebases' method.
     */
    private void sendEmailVerification(){

        showLoadingDialog(getString(R.string.sendinglingk));

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Snackbar.make(findViewById(R.id.Fragment), getString(R.string.sentvlink),
                                    Snackbar.LENGTH_LONG)
                                    .show();

                            Log.d(TAG, "Lnk sent to: "+user.getEmail());

                        } else {

                            FirebaseException e = (FirebaseException) task.getException();
                            Log.d(TAG, "CREATION_FAILED: "+e.getMessage());

                            Toast.makeText(MainActivity.this, "Failed to send verification mail.", Toast.LENGTH_SHORT)
                                    .show();

                        }

                        hideLoadingDialog();
                    }
                });
    }

    /*
        The callback function from RegisterFragment. Used to get registration details from the fragment
        to this mainActivity, which can be used to create the user.
     */
    @Override
    public void onRegisterAttempt(String name, String email, String pw) {
        createNewUser(name, email);
        createAccount(email, pw);
    }

    /*
        The callback function from LoginFragment. Used to get login details from the fragment
        to this mainActivity, which can be used to login the user.
     */
    @Override
    public void OnLoginAttempt(String email, String pw) {
        signIn(email, pw);
    }


    /*
       The callback function from Signoutragment. Used to get login details from the fragment
       to this mainActivity, which can be used to signout the user.
    */
    @Override
    public void onSignoutAttempt() {
        signOut();
    }


    /*
       If the fragment stack is empty then display the welcome screen once, and if the back button is pressed,
       then exit the app.
        */
    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() == 0 && BACK_TO_EXIT != 1){
            BACK_TO_EXIT++;

            Toast.makeText(this, "Press 'Back' once more to exit!", Toast.LENGTH_SHORT).show();

            goToFragment(mAuth.getCurrentUser());

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



}
