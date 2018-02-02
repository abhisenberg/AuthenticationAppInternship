package com.abheisenberg.sampleinternshipapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.OnSignoutAttempt} interface
 * to handle interaction events.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener{

    private OnSignoutAttempt signoutAttempt;

    private TextView tv_helloname;
    private Button bt_signout;
    private View rootView;

    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        tv_helloname = (TextView) rootView.findViewById(R.id.tv_wf_hello);
        bt_signout = (Button) rootView.findViewById(R.id.bt_wf_signout);

        tv_helloname.setOnClickListener(this);
        bt_signout.setOnClickListener(this);

        return rootView;
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
