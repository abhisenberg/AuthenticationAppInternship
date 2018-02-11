package com.abheisenberg.sampleinternshipapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static com.abheisenberg.sampleinternshipapp.BaseActivity.KEY_NAME;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.OnSignoutAttempt} interface
 * to handle interaction events.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "wf";

    private OnSignoutAttempt signoutAttempt;

    private TextView tv_helloname;
    private Button bt_signout;
    private View rootView;
    private String name = "";

    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        getUserName();

        tv_helloname = (TextView) rootView.findViewById(R.id.tv_wf_hello);
        bt_signout = (Button) rootView.findViewById(R.id.bt_wf_signout);

        bt_signout.setOnClickListener(this);

        return rootView;
    }

    private void getUserName() {
        DatabaseReference mdb = FirebaseDatabase.getInstance().getReference("users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String userKey = mAuth.getCurrentUser().getEmail().split("@")[0];

        Log.d(TAG, "getUserName key : "+userKey);

        /*
            Here we get the name of the user from the database, by using the email adress
            of the user.
         */
        mdb.child("users").child(userKey)
                .child(KEY_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
                Log.d(TAG, "onDataChange: NAME "+name );
                tv_helloname.setTextColor(Color.BLACK);
                tv_helloname.setText(getString(R.string.hello, name));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tv_helloname.setText("Error fetching data!");
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (signoutAttempt != null) {
            signoutAttempt.onSignoutAttempt();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignoutAttempt) {
            signoutAttempt = (OnSignoutAttempt) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSignoutAttempt");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signoutAttempt = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        /*
            Only one button in this fragment, to sign out.
         */

        switch (id){
            case R.id.bt_wf_signout:
                signoutAttempt.onSignoutAttempt();
                break;
        }
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
    public interface OnSignoutAttempt {
        // TODO: Update argument type and name
        void onSignoutAttempt();
    }
}
