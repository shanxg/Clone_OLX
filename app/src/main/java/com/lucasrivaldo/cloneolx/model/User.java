package com.lucasrivaldo.cloneolx.model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;

import static com.lucasrivaldo.cloneolx.activity.MainActivity.TAG;
import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.USERS;

public class User {
    private String email, id;

    public User() {
    }

    public boolean save(){

        DatabaseReference usersRef = ConfigurateFirebase.getFireDBRef()
                .child(USERS).child(this.getId());

        try {
            usersRef.setValue(this);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "save: "+e.getMessage());
            return false;
        }
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
