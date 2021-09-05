package com.lucasrivaldo.cloneolx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;
import com.lucasrivaldo.cloneolx.helper.AlertDialogUtil;
import com.lucasrivaldo.cloneolx.helper.SystemPermissions;
import com.lucasrivaldo.cloneolx.helper.UserFirebase;
import com.lucasrivaldo.cloneolx.model.Announcement;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.IMAGES;
import static com.lucasrivaldo.cloneolx.activity.MainActivity.TAG;

public class RegisterAnnouncementActivity extends AppCompatActivity
        implements View.OnClickListener {

    private String[] mPhotoStringHolder = new String[3];
    private List<String> mPhotoList = new ArrayList<>();
    private List<String> mPhotos;
    private Announcement myAnnouncement;

    private CurrencyEditText mEditPrice;
    private EditText mEditTitle, mEditDescription;
    private ImageView mImage, mImage1, mImage2;
    private Spinner mSpinnerRegions, mSpinnerCategory;
    private MaskEditText mEditContact;
    private AlertDialog mDialog;

    /** ####################################  INITIALIZE  #################################### **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_announcement);

        SystemPermissions.validatePermissions(this, 1);

        preLoad();
        loadInterface();
    }

    private void preLoad() {
        mPhotos = new ArrayList<>();
    }

    private void loadInterface() {
        mEditTitle = findViewById(R.id.editTitle);
        mEditDescription = findViewById(R.id.editDescription);
        mEditPrice = findViewById(R.id.editPrice);
        mEditContact = findViewById(R.id.editContact);

        mImage = findViewById(R.id.announceImage);
        mImage1 = findViewById(R.id.announceImage1);
        mImage2 = findViewById(R.id.announceImage2);

        mSpinnerRegions = findViewById(R.id.spinnerRegions);
        mSpinnerCategory = findViewById(R.id.spinnerCategory);
        loadSpinnersData();

        Locale currentLocale = new Locale("pt","BR");
        mEditPrice.setLocale(currentLocale);

        setClickListeners();
    }

    private void loadSpinnersData() {
        String[] regions = getResources().getStringArray(R.array.regions);
        ArrayAdapter<String> adapterRegions =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regions);
        adapterRegions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRegions.setAdapter(adapterRegions);

        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapterCategories =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCategory.setAdapter(adapterCategories);
    }

    /** ##################################  CLICK LISTENERS  ################################## **/

    private void setClickListeners(){
        mImage.setOnClickListener(this);
        mImage1.setOnClickListener(this);
        mImage2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        switch (itemId){
            case R.id.announceImage:
            case R.id.announceImage1:
            case R.id.announceImage2:

                ImageView iv = (ImageView) view;

                if (!iv.getDrawable().equals(getDrawable(R.drawable.padrao))
                        || iv.getDrawable() != null) {
                    int idNum = getIdNum(itemId);
                    mPhotoList.remove(mPhotoStringHolder[idNum]);
                }

                chooseImg(itemId);

                break;
        }
    }

    // CLICK LISTENER FOR BUTTON REGISTER ANNOUNCEMENT
    public void registerAnnouncement(View view) {
        if (validateAnnouncementData()) {
            throwToast("Announcement complete!\nPublishing announcement data.", false);
        }
    }

    /** ####################################  MY METHODS  #################################### **/

    private boolean validateAnnouncementData(){

        if (!validateText())
            return false;

        else if (mPhotoList.size() < 1) {

            throwToast(getResources().getString(R.string.text_minimum_imgs),true);
            return false;

        }else {

            mDialog = AlertDialogUtil.progressDialogAlert(this);
            mDialog.setOnDismissListener(dialogInterface -> finish());

            mDialog.show();

            return uploadImages();
        }
    }

    private boolean validateText() {

        String title = mEditTitle.getText().toString();
        long price = mEditPrice.getRawValue();
        String description = mEditDescription.getText().toString();
        String contact = mEditContact.getUnMasked();
        String region = mSpinnerRegions.getSelectedItem().toString();
        String category = mSpinnerCategory.getSelectedItem().toString();

        String selectRegion = getResources().getStringArray(R.array.regions)[0];
        String selectCategory = getResources().getStringArray(R.array.categories)[0];

        if (title.isEmpty()) {

            throwToast("Announcement title is empty!", true);
            return false;

        } else if (price == 0) {

            throwToast("Announcement price is empty!", true);
            return false;

        } else if (description.isEmpty()) {

            throwToast("Announcement description is empty!", true);
            return false;

        } else if (contact.isEmpty() || contact.length() < 11) {

            throwToast("User contact is not complete!", true);
            return false;

        } else if (region.equals(selectRegion)) {

            throwToast("Choose a " + selectRegion, true);
            return false;

        } else if (category.equals(selectCategory)) {

            throwToast("Choose a " + selectCategory, true);
            return false;

        } else {

            myAnnouncement = new Announcement();
            myAnnouncement.setOwnerId(UserFirebase.getCurrentUserID());

            myAnnouncement.setRegion(region);
            myAnnouncement.setCategory(category);

            myAnnouncement.setTitle(title);

            String value = mEditPrice.getText().toString();
            myAnnouncement.setPrice(value);
            myAnnouncement.setDescription(description);
            myAnnouncement.setContact(contact);

            return true;
        }
    }

    private boolean uploadImages() {

        StorageReference announcesImgRef =
                ConfigurateFirebase.getStorageRef()
                        .child(IMAGES)
                        .child(myAnnouncement.getId());

        try {
            for (String photo : mPhotoList) {
                int i = mPhotoList.indexOf(photo);

                StorageReference imgRef = announcesImgRef.child("image" + i);

                UploadTask uploadTask = imgRef.putFile(Uri.parse(photo));
                uploadTask.addOnCompleteListener(task ->
                                imgRef.getDownloadUrl().addOnCompleteListener(task1 ->{

                                    String photoUrl = task1.getResult().toString();
                                    mPhotos.add(photoUrl);

                                    myAnnouncement.setPhotos(mPhotos);

                                    if(myAnnouncement.save()){
                                        mDialog.dismiss();
                                    }
                                })
                );
            }
            return true;

        }catch (Exception e){
            e.printStackTrace();
            throwToast(e.getMessage(), true);
            Log.d(TAG, "uploadImages: " + e.getMessage());

            return false;
        }
    }

    /** #################################  ACTIVITY PROCESS  ################################## **/
    @Override
    public void onRequestPermissionsResult
    (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissionResult: grantResults){
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                AlertDialogUtil.permissionValidationAlert(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
         Uri selectedImage = data.getData();
         String imageAddress = selectedImage.toString();

            if (requestCode == 0){
                mImage.setImageURI(selectedImage);
            }else if (requestCode == 1){
                mImage1.setImageURI(selectedImage);
            }else if (requestCode == 2){
                mImage2.setImageURI(selectedImage);
            }
            mPhotoStringHolder[requestCode] = imageAddress;
            mPhotoList.add(imageAddress);
        }
    }

    /** ####################################  HELPERS  #################################### **/

    private int getIdNum(int itemId) {
        int idNum = itemId;
        switch (itemId){
            case R.id.announceImage:
                idNum =  0;
                break;
            case R.id.announceImage1:
                idNum =  1;
                break;
            case R.id.announceImage2:
                idNum =  2;
                break;
        }
        return idNum;
    }

    private void chooseImg(int itemId) {
        Intent galleryIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int requestCode;
        if (itemId == mImage.getId()){
            requestCode = 0;
        }else if (itemId == mImage1.getId()){
            requestCode = 1;
        }else{
            requestCode = 2;
        }

        startActivityForResult(galleryIntent, requestCode);
    }

    private void throwToast(String message, boolean isLong) {
        Toast.makeText(this, message,
                isLong ? Toast.LENGTH_LONG :Toast.LENGTH_SHORT).show();
    }
}
