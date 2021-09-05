package com.lucasrivaldo.cloneolx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.helper.OlxHelper;
import com.lucasrivaldo.cloneolx.helper.UserFirebase;
import com.lucasrivaldo.cloneolx.model.Announcement;
import com.santalu.maskara.widget.MaskEditText;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.List;

public class AnnouncementsDetailsActivity extends AppCompatActivity
        implements View.OnClickListener {

    private Announcement mAnnouncement;

    private CarouselView mCarouselView;
    private TextView mTextDetailTitle, mTextDetailPrice,
    mTextDetailDescription, mTextDetailRegion;
    private MaskEditText mTextDetailContact;
    private Button mButtonCotact;

    /** ####################################  INITIALIZE  #################################### **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_details);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            mAnnouncement = (Announcement) bundle.getSerializable("detail");

            if (mAnnouncement != null) {
                preLoad();
                loadInterface();
            }
        }
    }

    private void preLoad() {


    }

    private void loadInterface() {

        mCarouselView = findViewById(R.id.carouselView);

        mTextDetailTitle = findViewById(R.id.textDetailTitle);
        mTextDetailPrice  = findViewById(R.id.textDetailPrice);
        mTextDetailDescription  = findViewById(R.id.textDetailDescription);
        mTextDetailRegion  = findViewById(R.id.textDetailRegion);
        mTextDetailContact = findViewById(R.id.textDetailContact);

        mButtonCotact  = findViewById(R.id.buttonCotact);
        mButtonCotact.setOnClickListener(this);

        loadView();
    }

    /** ##################################  CLICK LISTENERS  ################################## **/

    @Override
    public void onClick(View view) {

        if (UserFirebase.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else {
            toggleContact(true);

            Intent i  = new Intent(Intent.ACTION_DIAL,
                    Uri.fromParts("tel", mAnnouncement.getContact(), null));
            startActivity(i);
        }
    }

    /** #####################################  LISTENERS  ##################################### **/

    /** ####################################  MY METHODS  #################################### **/

    private void loadView(){
        mTextDetailTitle.setText(mAnnouncement.getTitle());
        mTextDetailPrice.setText(mAnnouncement.getPrice());
        mTextDetailRegion.setText(mAnnouncement.getRegion());
        mTextDetailDescription.setText(mAnnouncement.getDescription());


        String contact = mAnnouncement.getContact();
        String number = OlxHelper.formatPhoneNumber(contact);

        mTextDetailContact.setText(number);
        mTextDetailContact.setEnabled(false);
        mTextDetailContact.setClickable(false);


        List<String> photos = mAnnouncement.getPhotos();

        ImageListener imageListener = (position, imageView) -> {
            String photoString = photos.get(position);
            Glide.with(this).load(Uri.parse(photoString)).into(imageView);
        };

        mCarouselView.setPageCount(photos.size());
        mCarouselView.setImageListener(imageListener);
    }

    /** ###############################  ACTIVITY LIFE-CYCLE  ################################# **/

    /** #################################  ACTIVITY PROCESS  ################################## **/

    /** ####################################  HELPERS  #################################### **/

    private void toggleContact(boolean isOpening){
        int gone = View.GONE;
        int visible = View.VISIBLE;

        if (isOpening){

            mButtonCotact.setVisibility(gone);
            mTextDetailContact.setVisibility(visible);

        }else {

            mButtonCotact.setVisibility(visible);
            mTextDetailContact.setVisibility(gone);
        }
    }

    private void throwToast(String message, boolean isLong) {
        Toast.makeText(this, message,
                isLong ? Toast.LENGTH_LONG :Toast.LENGTH_SHORT).show();
    }
}


