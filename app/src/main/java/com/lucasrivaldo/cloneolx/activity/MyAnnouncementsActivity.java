package com.lucasrivaldo.cloneolx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.adapter.AdapterAnnouncements;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;
import com.lucasrivaldo.cloneolx.helper.RecyclerItemClickListener;
import com.lucasrivaldo.cloneolx.helper.UserFirebase;
import com.lucasrivaldo.cloneolx.model.Announcement;

import java.util.ArrayList;
import java.util.List;

import static com.lucasrivaldo.cloneolx.activity.MainActivity.TAG;
import static com.lucasrivaldo.cloneolx.activity.MainActivity.TEST;
import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.ANNOUNCES;
import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.MY_ANNOUNCES;

public class MyAnnouncementsActivity extends AppCompatActivity {

    private List<Announcement> mAnnouncementList;

    private AdapterAnnouncements mAdapterAnnouncements;
    private RecyclerView mRecyclerAnnouncements;

    private DatabaseReference myAnnouncesRef;

    /** ####################################  INITIALIZE  #################################### **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preLoad();
        loadInterface();
    }

    private void preLoad() {
        setContentView(R.layout.activity_my_announcements);

        mAnnouncementList = new ArrayList<>();

        myAnnouncesRef = ConfigurateFirebase.getFireDBRef()
                                            .child(MY_ANNOUNCES)
                                            .child(UserFirebase.getCurrentUserID());
        getMyAnnouncesData();
    }

    private void loadInterface() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerAnnouncements = findViewById(R.id.recyclerAnnouncements);

        mAdapterAnnouncements = new AdapterAnnouncements(mAnnouncementList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerAnnouncements.setLayoutManager(layoutManager);
        mRecyclerAnnouncements.setHasFixedSize(false);
        mRecyclerAnnouncements.setAdapter(mAdapterAnnouncements);
        setRecyclerAnnouncementsClickListener();

        setClickListeners();
    }

    //TODO OPEN ANNOUNCEMENTS DETAILS
    private void setRecyclerAnnouncementsClickListener() {
        RecyclerItemClickListener recyclerClickListener =
                new RecyclerItemClickListener(
                        this,
                        mRecyclerAnnouncements,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                /*
                                    Intent detailIntent =
                                            new Intent(MyAnnouncementsActivity.this,
                                                    AnnouncementsDetailsActivity.class);

                                    detailIntent.putExtra("detail", mAnnouncementList.get(position));
                                    startActivity(detailIntent);
                                    */

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Announcement announcement = mAnnouncementList.get(position);

                                //announcement.cancel();
                                cancel(announcement);


                            }

                            @Override
                            public void onItemClick
                                    (AdapterView<?> adapterView, View view, int i, long l) {
                            }
                        });

        mRecyclerAnnouncements.addOnItemTouchListener(recyclerClickListener);
    }

    private void cancel(Announcement announcement) {

        DatabaseReference announcesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(ANNOUNCES)
                        .child(announcement.getRegion())
                        .child(announcement.getCategory())
                        .child(announcement.getId());

        DatabaseReference myAnnouncesRef =
                ConfigurateFirebase.getFireDBRef()
                        .child(MY_ANNOUNCES)
                        .child(announcement.getOwnerId())
                        .child(announcement.getId());

        announcesRef.removeValue();
        myAnnouncesRef.removeValue();

        mAnnouncementList.remove(announcement);
        mAdapterAnnouncements.notifyDataSetChanged();
        throwToast("Announcement cancelled.", false);
    }

    /** ##################################  CLICK LISTENERS  ################################## **/

    private void setClickListeners() {
        FloatingActionButton fab = findViewById(R.id.fabNewAnnounce);
        fab.setOnClickListener(view ->
            startActivity(new Intent(this, RegisterAnnouncementActivity.class)));
    }


    /** #####################################  LISTENERS  ##################################### **/

    private void getMyAnnouncesData() {

        myAnnouncesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAnnouncementList.clear();

                for (DataSnapshot announcementsData : snapshot.getChildren()) {
                    Announcement announcement = announcementsData.getValue(Announcement.class);
                    if (announcement != null) mAnnouncementList.add(announcement);
                }

                mAdapterAnnouncements.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    /** ####################################  MY METHODS  #################################### **/
    /** ###############################  ACTIVITY LIFE-CYCLE  ################################# **/
    @Override
    protected void onResume() {
        super.onResume();
        if (myAnnouncesRef!=null) getMyAnnouncesData();
    }

    /** #################################  ACTIVITY PROCESS  ################################## **/
    /** ####################################  HELPERS  #################################### **/

    private void throwToast(String message, boolean isLong) {
        Toast.makeText(this, message,
                isLong ? Toast.LENGTH_LONG :Toast.LENGTH_SHORT).show();
    }
}
