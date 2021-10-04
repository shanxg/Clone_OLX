package com.lucasrivaldo.cloneolx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.adapter.AdapterAnnouncements;
import com.lucasrivaldo.cloneolx.config.ConfigurateFirebase;
import com.lucasrivaldo.cloneolx.helper.AlertDialogUtil;
import com.lucasrivaldo.cloneolx.helper.OlxHelper;
import com.lucasrivaldo.cloneolx.helper.RecyclerItemClickListener;
import com.lucasrivaldo.cloneolx.helper.UserFirebase;
import com.lucasrivaldo.cloneolx.model.Announcement;

import java.util.ArrayList;
import java.util.List;

import static com.lucasrivaldo.cloneolx.config.ConfigurateFirebase.ANNOUNCES;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AlertDialogUtil.ReturnFilterData {

    public static final String TEST = "USER_TEST";
    public static final String TAG = "USER_TEST_ERROR";

    private boolean mIsForRegions;

    private List<Announcement> mAnnouncementList;

    private AdapterAnnouncements mAdapterAnnouncements;
    private RecyclerView mRecyclerAnnouncements;

    private DatabaseReference mAnnouncesRef;

    private Button mButtonRegion, mButtonCategory;

    /** ####################################  INITIALIZE  #################################### **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OlxHelper.detailsAlertLoopHolder) {
            AlertDialogUtil.openDetailsAlert(this);
            OlxHelper.detailsAlertLoopHolder = true;
        }

        preLoad();
        loadInterface();
    }

    private void preLoad() {
        mAnnouncementList = new ArrayList<>();

        mAnnouncesRef = ConfigurateFirebase.getFireDBRef().child(ANNOUNCES);

    }

    private void loadInterface() {

        mButtonRegion = findViewById(R.id.buttonRegion);
        mButtonCategory = findViewById(R.id.buttonCategory);

        mRecyclerAnnouncements = findViewById(R.id.recyclerAnnouncements);

        mAdapterAnnouncements = new AdapterAnnouncements(mAnnouncementList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerAnnouncements.setLayoutManager(layoutManager);
        mRecyclerAnnouncements.setHasFixedSize(false);
        mRecyclerAnnouncements.setAdapter(mAdapterAnnouncements);

        setRecyclerAnnouncementsClickListener();
        setClickListeners();
    }

    private void setRecyclerAnnouncementsClickListener() {
        RecyclerItemClickListener recyclerClickListener =
                new RecyclerItemClickListener(
                        this,
                        mRecyclerAnnouncements,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {}

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Intent detailIntent =
                                        new Intent(MainActivity.this,
                                                AnnouncementsDetailsActivity.class);

                                detailIntent.putExtra("detail", mAnnouncementList.get(position));
                                startActivity(detailIntent);
                            }

                            @Override
                            public void onItemClick
                                    (AdapterView<?> adapterView, View view, int i, long l) {}
                        });

        mRecyclerAnnouncements.addOnItemTouchListener(recyclerClickListener);
    }

    /** ##################################  CLICK LISTENERS  ################################## **/

    private void setClickListeners() {
        mButtonRegion.setOnClickListener(this);
        mButtonCategory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();


        switch (viewId){
            case R.id.buttonRegion:
                mIsForRegions = (true);
                AlertDialogUtil.filterTypeDialog(this, true, this);
                break;

            case R.id.buttonCategory:
                mIsForRegions = (false);
                AlertDialogUtil.filterTypeDialog(this, false, this);
                break;
        }
    }

    /** #####################################  LISTENERS  ##################################### **/

    public void getFilterData(String type){

        mAnnouncementList.clear();
        mAdapterAnnouncements.notifyDataSetChanged();

        DatabaseReference filterQueryRef;
        if (mIsForRegions)
            filterQueryRef = ConfigurateFirebase.getFireDBRef().child(ANNOUNCES).child(type);
        else
            filterQueryRef = ConfigurateFirebase.getFireDBRef().child(ANNOUNCES);

        filterQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (mIsForRegions) { // SET LOOP FOR REGION SELECTED

                    for (DataSnapshot categories : snapshot.getChildren()) {
                        for (DataSnapshot announcementsData : categories.getChildren()) {

                            if (announcementsData != null) {
                                Announcement announcement =
                                        announcementsData.getValue(Announcement.class);

                                mAnnouncementList.add(announcement);
                            }
                        }
                    }

                }else { // SET LOOP FOR CATEGORIES SELECTED

                    for (DataSnapshot region : snapshot.getChildren())
                        for (DataSnapshot category : region.getChildren())
                            if (category.getKey().equals(type))
                                for (DataSnapshot announcementsData : category.getChildren()) {

                                    if (announcementsData != null) {

                                        Announcement announcement =
                                                announcementsData.getValue(Announcement.class);
                                        mAnnouncementList.add(announcement);
                                    }
                                }
                }

                mAdapterAnnouncements.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getAnnouncementData() {
        mAnnouncementList.clear();
        mAdapterAnnouncements.notifyDataSetChanged();

        mAnnouncesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot regions : snapshot.getChildren())
                    for (DataSnapshot categories : regions.getChildren())
                        for (DataSnapshot announcementsData : categories.getChildren()) {
                            Announcement announcement =
                                    announcementsData.getValue(Announcement.class);
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

        getAnnouncementData();
        invalidateOptionsMenu();
    }

    /** #################################  ACTIVITY PROCESS  ################################## **/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.setGroupVisible(R.id.group_logged,UserFirebase.getCurrentUser()!=null );
        menu.setGroupVisible(R.id.group_unlogged, UserFirebase.getCurrentUser()==null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_refresh:
                getAnnouncementData();

                break;

            case R.id.signOut:
                UserFirebase.signOut();
                invalidateOptionsMenu();

                break;

            case R.id.btnSignIn:
                startActivity(new Intent(this, LoginActivity.class));

                break;

            case R.id.menu_announcements:
                startActivity(new Intent(this, MyAnnouncementsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /** ####################################  HELPERS  #################################### **/

    private void throwToast(String message, boolean isLong) {
        Toast.makeText(this, message,
                isLong ? Toast.LENGTH_LONG :Toast.LENGTH_SHORT).show();
    }
}


