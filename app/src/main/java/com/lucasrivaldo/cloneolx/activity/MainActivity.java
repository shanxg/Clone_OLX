package com.lucasrivaldo.cloneolx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.helper.UserFirebase;

public class MainActivity extends AppCompatActivity {

    public static final String TEST = "USER_TEST";
    public static final String TAG = "USER_TEST_ERROR";

    private MenuItem mBtnSignOut, mBtnSignIn;

    /** ####################################  INITIALIZE  #################################### **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** ##################################  CLICK LISTENERS  ################################## **/

    /** ####################################  MY METHODS  #################################### **/


    /** ###############################  ACTIVITY LIFE-CYCLE  ################################# **/

    @Override
    protected void onResume() {
        super.onResume();
        updateToolbar();
    }

    /** #################################  ACTIVITY PROCESS  ################################## **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mBtnSignIn = menu.findItem(R.id.btnSignIn);
        mBtnSignOut = menu.findItem(R.id.signOut);

        updateToolbar();

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOut){
            UserFirebase.signOut();
            updateToolbar();

        }else if (item.getItemId() == R.id.btnSignIn){
            startActivity(new Intent(this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /** ####################################  HELPERS  #################################### **/

    private void updateToolbar(){
        toggleSignOut(UserFirebase.getCurrentUser() != null);
        toggleBtnSignIn(UserFirebase.getCurrentUser() == null);
    }

    private void toggleBtnSignIn(boolean isOpening) {
        if (mBtnSignIn!=null)
            mBtnSignIn.setVisible(isOpening);
    }

    private void toggleSignOut(boolean isOpening){
        if (mBtnSignOut!=null)
            mBtnSignOut.setVisible(isOpening);
    }

}

/** ####################################  INITIALIZE  #################################### **/
/** ##################################  CLICK LISTENERS  ################################## **/
/** ####################################  MY METHODS  #################################### **/
/** #################################  ACTIVITY PROCESS  ################################## **/
/** ####################################  HELPERS  #################################### **/
