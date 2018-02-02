package com.abheisenberg.sampleinternshipapp;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends BaseActivity implements RegisterFragment.OnRegisterAttemptListener, LoginFragment.OnLoginAttempt,
        WelcomeFragment.OnSignoutAttempt{

    private static final String TAG = "MainAct";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        goToFragment(mAuth.getCurrentUser());
    }

    private void goToFragment(FirebaseUser currentUser) {
        if(currentUser == null){
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        hideLoadingDialog();

                    }
                });
    }

    private void signOut(){
        mAuth.signOut();

        Toast.makeText(this, getString(R.string.signedout), Toast.LENGTH_SHORT).show();

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.Fragment, new LoginFragment())
                .commit();
    }

    private void createAccount(String email, String pw){
        Log.d(TAG, "createAccount: "+email);

        showLoadingDialog();

        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .replace(R.id.Fragment, new LoginFragment())
                                    .commit();

                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.registerfailed),
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideLoadingDialog();
                    }
                });
    }

    private void sendEmailVerification(){

        showLoadingDialog(getString(R.string.sendinglingk));

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Verification mail sent to "+ user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(MainActivity.this, "Failed to send verification mail.", Toast.LENGTH_SHORT)
                                    .show();

                        }

                        hideLoadingDialog();
                    }
                });
    }

    @Override
    public void onRegisterAttempt(String name, String email, String pw) {
        createAccount(email, pw);
    }

    @Override
    public void OnLoginAttempt(String email, String pw) {
        signIn(email, pw);
    }

    @Override
    public void onSignoutAttempt() {
        signOut();
    }
}
