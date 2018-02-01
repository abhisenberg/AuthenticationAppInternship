package com.abheisenberg.sampleinternshipapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainAct";

    private FirebaseAuth mAuth;

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

        bt_signin.setOnClickListener(this);
        bt_signout.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_sendVerificatoinMail.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSreenUI(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void createAccount(String email, String pw){
        Log.d(TAG, "createAccount: "+email);
        if(!validateForm()){
            return;
        }

        showLoadingDialog();

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

                        hideLoadingDialog();
                    }
                });
    }

    private void signIn(String email, String pw){
        Log.d(TAG, "signIn attempt from "+email);

        if(!validateForm()){
            return;
        }

        showLoadingDialog();

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
                            tv_status.setText("No Account! Please register!");
                            tv_status.setTextColor(Color.parseColor("#ff9a4c"));
                        }

                        hideLoadingDialog();

                    }
                });
    }

    private void signOut(){
        mAuth.signOut();
        updateSreenUI(null);
    }

    private void sendEmailVerification(){
        bt_sendVerificatoinMail.setEnabled(false);

        showLoadingDialog();

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        bt_sendVerificatoinMail.setEnabled(true);

                        if(task.isSuccessful()){
                            Log.d(TAG, "Verification mail sent to  "+user.getEmail());
                            Toast.makeText(MainActivity.this, "Verification mail sent to "+ user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Verification mail not sent ", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to send verification mail.", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        hideLoadingDialog();
                    }
                });
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
            bt_sendVerificatoinMail.setVisibility(View.GONE);
        }
        else {
            //Signed in
            et_emailid.setVisibility(View.GONE);
            et_pw.setVisibility(View.GONE);
            bt_signin.setVisibility(View.GONE);
            bt_register.setVisibility(View.GONE);
            bt_signout.setVisibility(View.VISIBLE);


            if(user.isEmailVerified()){
                tv_status.setText("Hello "+user.getDisplayName()+"!");
                tv_status.setTextColor(Color.parseColor("#c70039"));
                bt_sendVerificatoinMail.setVisibility(View.GONE);
            } else {
                tv_status.setText("Please verify your email from the link!");
                tv_status.setTextColor(Color.parseColor("#ff9a4c"));
                bt_sendVerificatoinMail.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onClick(View v) {

        hideKeyboard(this);

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
