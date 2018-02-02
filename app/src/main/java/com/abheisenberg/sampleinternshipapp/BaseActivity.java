package com.abheisenberg.sampleinternshipapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by abheisenberg on 31/1/18.
 */

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog myLoadingDialog;

    public void showLoadingDialog(){
        if(myLoadingDialog == null){
            myLoadingDialog = new ProgressDialog(this);
            myLoadingDialog.setMessage(getString(R.string.loading));
            myLoadingDialog.setIndeterminate(true);
        }

        myLoadingDialog.show();
    }

    public void showLoadingDialog(String s){
        if(myLoadingDialog == null){
            myLoadingDialog = new ProgressDialog(this);
            myLoadingDialog.setMessage(s);
            myLoadingDialog.setIndeterminate(true);
        }

        myLoadingDialog.show();
    }

    public void hideLoadingDialog(){
        if(myLoadingDialog != null && myLoadingDialog.isShowing()){
            myLoadingDialog.dismiss();
        }
    }

    public void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null){
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideLoadingDialog();
    }
}
