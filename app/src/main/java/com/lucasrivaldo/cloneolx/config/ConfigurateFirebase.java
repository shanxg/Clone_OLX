package com.lucasrivaldo.cloneolx.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigurateFirebase {

    public static final String USERS = "users";
    public static final String xx = "x";
    public static final String xxx = "x";
    public static final String xxxx = "x";
    public static final String xxxxx = "x";

    public static final String xxxxxxxxxx  = "x";
    public static final String xxxxxxxxxxx  = "x";


    private static FirebaseAuth mAuth;
    private static DatabaseReference mFireDBRef;
    private static StorageReference mStorage;


    public static DatabaseReference getFireDBRef(){

        if(mFireDBRef ==null){
            mFireDBRef = FirebaseDatabase.getInstance().getReference();
        }

        return mFireDBRef;
    }


    public static FirebaseAuth getFirebaseAuth() {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        return mAuth;
    }

    public static StorageReference getStorageRef(){
        if (mStorage == null)
            mStorage = FirebaseStorage.getInstance().getReference();

        return mStorage;
    }
}
