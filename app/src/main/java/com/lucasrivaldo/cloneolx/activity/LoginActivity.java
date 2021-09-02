package com.lucasrivaldo.cloneolx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;
import com.lucasrivaldo.cloneolx.helper.UserFirebase;
import com.lucasrivaldo.cloneolx.model.User;

import static com.lucasrivaldo.cloneolx.activity.MainActivity.TAG;
import static com.lucasrivaldo.cloneolx.activity.MainActivity.TEST;

public class LoginActivity extends AppCompatActivity {

    private static final String TYPE_SIGN = "sign";
    private static final String TYPE_REG = "reg";

    private EditText mEditTextUserEmail, mEditTextUserPW;
    private TextView mTextButtonSwitchReg, mTextButtonSwitchSign;
    private Switch mSwitchType;
    private Button mButtonSignIn;

    /** ####################################  INITIALIZE  #################################### **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadInterface();
    }

    private void loadInterface() {
        mEditTextUserEmail = findViewById(R.id.editEmail);
        mEditTextUserPW = findViewById(R.id.editePW);
        mButtonSignIn = findViewById(R.id.buttonLogin);


        mTextButtonSwitchReg = findViewById(R.id.textButtonSwitchReg);
        mTextButtonSwitchSign = findViewById(R.id.textButtonSwitchSign);
        mSwitchType = findViewById(R.id.switchType);

        setClickListeners();
    }

    /** ##################################  CLICK LISTENERS  ################################## **/

    private void setClickListeners(){

        mButtonSignIn.setOnClickListener(view -> {

            String emailText = mEditTextUserEmail.getText().toString();
            String pwText = mEditTextUserPW.getText().toString();

            if (getUserType().equals(TYPE_SIGN)) {
                if (validateText(emailText, pwText))
                    authUser(emailText, pwText);
            }else {
                if (validateText(emailText, pwText))
                    registerUser(emailText, pwText);
            }
        });

        mTextButtonSwitchReg.setOnClickListener
                (view ->{
                    mSwitchType.setChecked(true);
                    setLogTypeTextColor();
                });

        mTextButtonSwitchSign.setOnClickListener
                (view ->{
                    mSwitchType.setChecked(false);
                    setLogTypeTextColor();
                });
        mSwitchType.setOnCheckedChangeListener
                ((compoundButton, isChecked) -> setLogTypeTextColor());
    }


    /** ####################################  MY METHODS  #################################### **/

    private boolean validateText(String emailText, String pwText){

        if (emailText.isEmpty()) {

            throwToast("User email text is empty", true);
            return false;

        }else if (pwText.isEmpty()){

            throwToast("User password text is empty", true);
            return false;

        }else
            return true;
    }

    private void authUser(String emailText, String pwText){

        ConfigurateFirebase.getFirebaseAuth()
                .signInWithEmailAndPassword(emailText, pwText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        startMainActivity();
                        throwToast(getResources().getString(R.string.text_sign_complete), false);

                    }else {

                        try { throw task.getException(); }
                        catch (Exception e) {
                            e.printStackTrace();
                            throwToast(e.getMessage(), true);
                            Log.d(TAG, "LoginActivity - authUser: "+ e.getMessage());
                        }
                    }
                });
    }

    private void registerUser(String emailText, String pwText) {

        ConfigurateFirebase.getFirebaseAuth()
                .createUserWithEmailAndPassword(emailText, pwText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        saveUser(emailText);
                        throwToast(getResources().getString(R.string.text_reg_complete), false);

                    }else {

                        try { throw task.getException(); }
                        catch (Exception e) {
                            e.printStackTrace();
                            throwToast(e.getMessage(), true);
                            Log.d(TAG, "LoginActivity - registerUser: "+ e.getMessage());
                        }
                    }
                });
    }

    private void saveUser(String emailText) {
        User user = new User();
        user.setEmail(emailText);
        user.setId(UserFirebase.getCurrentUserID());

        if (user.save()) startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /** ####################################  HELPERS  #################################### **/


    private void setLogTypeTextColor(){
        if (mSwitchType.isChecked()) {
            mTextButtonSwitchReg.setTextColor(getResources().getColor(R.color.colorAccent));
            mTextButtonSwitchSign.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            mTextButtonSwitchSign.setTextColor(getResources().getColor(R.color.colorAccent));
            mTextButtonSwitchReg.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private String getUserType(){
        return  mSwitchType.isChecked() ? TYPE_REG :  TYPE_SIGN;
    }

    private void throwToast(String message, boolean isLong) {
        Toast.makeText(this, message,
                isLong ? Toast.LENGTH_LONG :Toast.LENGTH_SHORT).show();
    }

}