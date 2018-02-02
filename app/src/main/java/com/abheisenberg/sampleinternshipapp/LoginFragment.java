package com.abheisenberg.sampleinternshipapp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginAttempt} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    private OnLoginAttempt loginAttempt;

    private View rootView;
    private FragmentTransaction fTrans;
    private Button bt_signin, bt_createAccount;
    private EditText et_pw, et_email;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        et_email = (EditText) rootView.findViewById(R.id.et_lf_emailid);
        et_pw = (EditText) rootView.findViewById(R.id.et_lf_pw);
        bt_createAccount = (Button) rootView.findViewById(R.id.bt_lf_register);
        bt_signin = (Button) rootView.findViewById(R.id.bt_lf_signin);

        bt_signin.setOnClickListener(this);
        bt_createAccount.setOnClickListener(this);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String email, String pw) {
        if (loginAttempt != null) {
            loginAttempt.OnLoginAttempt(email, pw);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginAttempt) {
            loginAttempt = (OnLoginAttempt) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginAttempt");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginAttempt = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        fTrans = getFragmentManager().beginTransaction();

        switch(id){
            case R.id.bt_lf_signin:

                /*
                Call the callback method on main activity to authenticate the login attempt.
                 */

                if(validateForm()){
                    onButtonPressed(
                            et_email.getText().toString(),
                            et_pw.getText().toString()
                    );
                }
                break;

            case R.id.bt_lf_register:
                fTrans.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                fTrans.replace(R.id.Fragment, new RegisterFragment());
                fTrans.commit();
                break;
        }
    }

    public boolean validateForm(){
        boolean isValid = true;

        if(TextUtils.isEmpty(et_email.getText().toString())){
            et_email.setError("Required");
            isValid = false;
        }
        if(TextUtils.isEmpty(et_pw.getText().toString())){
            et_pw.setError("Required");
            isValid = false;
        }
        return isValid;
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
    public interface OnLoginAttempt {
        // TODO: Update argument type and name
        void OnLoginAttempt(String email, String pw);
    }
}
