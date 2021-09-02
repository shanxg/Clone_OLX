package com.lucasrivaldo.cloneolx.helper;

import android.util.Log;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;

public class UserFirebase {

    public static final int GET_LOGGED_USER_DATA = -1;
    public static final int GET_X1_DATA = 0;
    public static final int GET_X2_DATA = 1;
    public static final int GET_X3_DATA = 10;

    public static final int GET_CURRENT_USER_LOC = 2;

    public static boolean signOut(){

        ConfigurateFirebase.getFirebaseAuth().signOut();

        return getCurrentUser() == null;
    }

    public static String getCurrentUserID() {

        return getCurrentUser().getUid();
    }

    public static FirebaseUser getCurrentUser() {

        return ConfigurateFirebase.getFirebaseAuth().getCurrentUser();
    }

    public static boolean updateUserProfName(String profileName) {

        try {
            FirebaseUser user = getCurrentUser();

            UserProfileChangeRequest userChangeRequest =
                    new UserProfileChangeRequest.Builder()
                            .setDisplayName(profileName)
                            .build();


            user.updateProfile(userChangeRequest).addOnCompleteListener(task -> {

                if (!task.isSuccessful()) {
                    Log.i("USER SAVE ERROR", "Error updating profile name at firebase user. \n" + task.getException().getMessage());
                }
            });
            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }

    public static void getLoggedUserData(String uType, ValueEventListener valueEventListener){
        DatabaseReference userRef = ConfigurateFirebase.getFireDBRef()
                                            .child(ConfigurateFirebase.USERS)
                                                .child(uType)
                                                    .child(getCurrentUserID());

        userRef.addListenerForSingleValueEvent(valueEventListener);
    }
}
