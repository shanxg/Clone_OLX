package com.lucasrivaldo.cloneolx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
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
        addSwipeMoves();
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
                            public void onLongItemClick(View view, int position) {}

                            @Override
                            public void onItemClick
                                    (AdapterView<?> adapterView, View view, int i, long l) {}
                        });

        mRecyclerAnnouncements.addOnItemTouchListener(recyclerClickListener);
    }

    public void addSwipeMoves(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START;
                //int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                deleteTransaction(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(mRecyclerAnnouncements);

    }

    public void deleteTransaction(RecyclerView.ViewHolder viewHolder){

        new Handler().post(() -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Delete announcement:");
            alertDialog.setMessage("Are you sure, you want to delete this announcement?");
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton("YES", (dialog, which) -> {

                int itemPosition = viewHolder.getAdapterPosition();
                Announcement announcement = mAnnouncementList.get(itemPosition);

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

                announcesRef.removeValue().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        myAnnouncesRef.removeValue()
                                .addOnCompleteListener(taskMyAnnouncement -> {

                                    if (taskMyAnnouncement.isSuccessful()) {

                                        throwToast("Announcement deleted", true);
                                        mAnnouncementList.remove(announcement);
                                        mAdapterAnnouncements.notifyItemRemoved(itemPosition);
                                        mAdapterAnnouncements.notifyDataSetChanged();


                                    } else {

                                        String exception;
                                        try {

                                            throw taskMyAnnouncement.getException();

                                        } catch (Exception e) {
                                            exception = e.getMessage();
                                            e.printStackTrace();
                                        }

                                        throwToast("Delete failure: \n" + exception, true);
                                    }
                                });


                        throwToast("Announcement cancelled.", false);


                    } else {

                        String exception;
                        try {

                            throw task.getException();

                        } catch (Exception e) {
                            exception = e.getMessage();
                            e.printStackTrace();
                        }

                        throwToast("Delete failure: \n" + exception, true);
                    }
                });



            });

            alertDialog.setNegativeButton("NO", (dialog, which) -> {

                throwToast("CANCELED",false);

                mAdapterAnnouncements.notifyDataSetChanged();
            });

            AlertDialog alert = alertDialog.create();
            alert.show();

        });
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
