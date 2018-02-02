package com.abheisenberg.sampleinternshipapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnRegisterAttemptListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private OnRegisterAttemptListener registerAttempt;

    private Button bt_register;
    private EditText et_name, et_email, et_pw;
    private View rootVIew;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootVIew = inflater.inflate(R.layout.fragment_register, container, false);

        bt_register = (Button) rootVIew.findViewById(R.id.bt_rf_register);
        et_name = (EditText) rootVIew.findViewById(R.id.et_rf_name);
        et_pw = (EditText) rootVIew.findViewById(R.id.et_rf_pw);
        et_email = (EditText) rootVIew.findViewById(R.id.et_rf_emailid);

        bt_register.setOnClickListener(this);
        et_name.setOnClickListener(this);
        et_pw.setOnClickListener(this);
        et_email.setOnClickListener(this);

        return rootVIew;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String name, String email, String pw) {
        if (registerAttempt != null) {
            registerAttempt.onRegisterAttempt(name, email, pw);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterAttemptListener) {
            registerAttempt = (OnRegisterAttemptListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterAttemptListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        registerAttempt = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_rf_register){

            if(validateForm()){

                onButtonPressed(
                        et_name.getText().toString(),
                        et_email.getText().toString(),
                        et_pw.getText().toString()
                );

                FragmentTransaction fTrans = getFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                fTrans.commit();
            }

        }
    }

    public boolean validateForm(){
        boolean isValid = true;

        if(TextUtils.isEmpty(et_name.getText().toString())){
            et_name.setError("Required");
            isValid = false;
        }
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
    public interface OnRegisterAttemptListener {
        // TODO: Update argument type and name
        void onRegisterAttempt(String name, String email, String pw);
    }
}
