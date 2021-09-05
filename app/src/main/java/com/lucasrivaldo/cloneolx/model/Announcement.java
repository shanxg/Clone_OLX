package com.lucasrivaldo.cloneolx.model;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;

import java.io.Serializable;
import java.util.List;

import static com.lucasrivaldo.cloneolx.activity.MainActivity.TAG;
import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.ANNOUNCES;
import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.MY_ANNOUNCES;

public class Announcement implements Serializable {

    private List<String> photos;
    private String region, category, title, price, description, contact, id;
    private String ownerId;

    public Announcement() { }

    public boolean save(){

        DatabaseReference announcesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(ANNOUNCES)
                        .child(this.getRegion())
                        .child(this.getCategory())
                        .child(this.getId());

        DatabaseReference myAnnouncesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(MY_ANNOUNCES)
                        .child(this.getOwnerId())
                        .child(this.getId());

        try {
            myAnnouncesRef.setValue(this);
            announcesRef.setValue(this);
            return true;

        }catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Announcement.save(): "+e.getMessage());

            return false;
        }
    }

    public void cancel() {

        DatabaseReference announcesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(ANNOUNCES)
                        .child(this.getRegion())
                        .child(this.getCategory())
                        .child(this.getId());

        DatabaseReference myAnnouncesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(MY_ANNOUNCES)
                        .child(this.getOwnerId())
                        .child(this.getId());


        myAnnouncesRef.removeValue(mRemoveListener);
        announcesRef.removeValue(mRemoveListener);

    }

    public void finish() {

        DatabaseReference announcesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(ANNOUNCES)
                        .child(this.getRegion())
                        .child(this.getCategory())
                        .child(this.getId());

        announcesRef.removeValue(mRemoveListener);
    }

    @Exclude
    public String getId() {
        if (this.id == null){
            this.id = ConfigurateFirebase.getFireDBRef().child(ANNOUNCES).push().getKey();
        }
        return id; }

    public String getOwnerId() { return ownerId; }
    public List<String> getPhotos() { return photos; }
    public String getRegion() { return region; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContact() { return contact; }
    public String getPrice() { return price; }

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public void setId(String id) { this.id = id; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
    public void setRegion(String region) { this.region = region; }
    public void setCategory(String category) { this.category = category; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setContact(String contact) { this.contact = contact; }
    public void setPrice(String price) { this.price = price; }


    private static DatabaseReference.CompletionListener mRemoveListener =
            (error, ref) -> {
                if (error == null) {
                    Log.d(TAG, "Removed: " + ref);
                    // or you can use:
                    System.out.println("Removed: " + ref);
                } else {
                    Log.e(TAG, "Remove of " + ref + " failed: " + error.getMessage());
                }
            };
}
