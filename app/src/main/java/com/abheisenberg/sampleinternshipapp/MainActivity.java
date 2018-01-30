package com.abheisenberg.sampleinternshipapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainAct";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextView tv_status;
    private EditText et_emailid, et_pw;
    private Button bt_signin, bt_signout ,bt_register ,bt_sendVerificatoinMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_status = (TextView) findViewById(R.id.tv_status);
        et_emailid = (EditText) findViewById(R.id.et_emailid);
        et_pw = (EditText) findViewById(R.id.et_pw);
        bt_signin = (Button) findViewById(R.id.bt_signin);
        bt_signout = (Button) findViewById(R.id.bt_signout);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_sendVerificatoinMail = (Button) findViewById(R.id.bt_sendverification);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in");
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Toast.makeText(MainActivity.this, "Signed_out", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void createAccount(String email, String pw){
        Log.d(TAG, "createAccount: "+email);
        if(!validateForm()){
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Successfully created the account, but not verified yet
                            Log.d(TAG, "Successfully created accnt(not verified yet)");
                            updateSreenUI(mAuth.getCurrentUser());
                        } else {
                            //Creation of account failed, display an error message to user
                            Log.w(TAG, "creation of user with E and P failed:  ", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            updateSreenUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String pw){
        Log.d(TAG, "signIn attempt from "+email);
        if(!validateForm()){
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Successfully signed in
                            Log.d(TAG, "Successfully signed in ");
                            updateSreenUI(mAuth.getCurrentUser());
                        } else {
                            //Sign in failed, display an error msg to user
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            updateSreenUI(null);
                            tv_status.setText("Authentication failed");
                        }
                    }
                });
    }

    private void signOut(){
        mAuth.signOut();
        updateSreenUI(null);
    }

    private void sendEmailVerification(){

    }

    private boolean validateForm(){
        boolean isValid = true;

        String email = et_emailid.getText().toString();
        if(TextUtils.isEmpty(email)){
            et_emailid.setError("Required");
            isValid = false;
        }

        String pw = et_pw.getText().toString();
        if(TextUtils.isEmpty(pw)){
            et_emailid.setError("Required");
            isValid = false;
        }

        return isValid;
    }

    private void updateSreenUI(FirebaseUser user){
        if(user == null){
            //Signed out
            tv_status.setText(R.string.signedout);
            et_emailid.setVisibility(View.VISIBLE);
            et_pw.setVisibility(View.VISIBLE);
            bt_signin.setVisibility(View.VISIBLE);
            bt_register.setVisibility(View.VISIBLE);
            bt_signout.setVisibility(View.GONE);
        } else {
            //Signed in
            tv_status.setText(R.string.signedin);
            et_emailid.setVisibility(View.GONE);
            et_pw.setVisibility(View.GONE);
            bt_signin.setVisibility(View.GONE);
            bt_register.setVisibility(View.GONE);
            bt_signout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onClick(View v) {
        int whichButton = v.getId();

        if(whichButton == R.id.bt_register){
            createAccount(et_emailid.getText().toString(), et_pw.getText().toString());

        } else if (whichButton == R.id.bt_signin){
            signIn(et_emailid.getText().toString(), et_pw.getText().toString());

        } else if (whichButton == R.id.bt_signout){
            signOut();

        } else if (whichButton == R.id.bt_sendverification){
            sendEmailVerification();
        }
    }
}
