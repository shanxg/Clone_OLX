package com.lucasrivaldo.cloneolx.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lucasrivaldo.cloneolx.R;
import com.lucasrivaldo.cloneolx.model.Announcement;

import java.util.List;

public class AdapterAnnouncements extends RecyclerView.Adapter<AdapterAnnouncements.AnnouncementsViewHolder> {

    private List<Announcement> announcements;
    private Context context;

    public AdapterAnnouncements(List<Announcement> announcements, Context context) {
        this.announcements = announcements;
        this.context = context;
    }

    class AnnouncementsViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewPrice, textViewTitle;
        private RecyclerView recyclerImages;

        AnnouncementsViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            recyclerImages = itemView.findViewById(R.id.recyclerImages);
        }
    }

    @NonNull
    @Override
    public AnnouncementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_announcement, parent, false);

        return new AnnouncementsViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementsViewHolder holder, int position) {
        Announcement announce = announcements.get(position);

        holder.textViewTitle.setText(announce.getTitle());
        holder.textViewPrice.setText(announce.getPrice());

        List<String> photos = announce.getPhotos();

        if (photos!=null) {

            AdapterImages adapterImages = new AdapterImages(photos, holder.recyclerImages);

            RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);

            holder.recyclerImages.setLayoutManager(layoutManager);
            holder.recyclerImages.setHasFixedSize(true);
            holder.recyclerImages.setAdapter(adapterImages);

            adapterImages.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    /** ################################### ADAPTER IMAGES ################################### **/

    class AdapterImages extends RecyclerView.Adapter<AdapterImages.ImageViewHolder> {

        private List<String> photos;
        private RecyclerView recyclerImages;


        AdapterImages(List<String> photos, RecyclerView recyclerImages) {
            this.photos = photos;
            this.recyclerImages = recyclerImages;
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {

            private ImageView btnPreviousImage, btnNextImage;
            private ImageView announceImage;

            private ImageViewHolder(@NonNull View itemView) {
                super(itemView);

                btnPreviousImage = itemView.findViewById(R.id.btnPreviousImage);
                announceImage = itemView.findViewById(R.id.announceImage);
                btnNextImage = itemView.findViewById(R.id.btnNextImage);
            }
        }

        @NonNull
        @Override
        public AdapterImages.ImageViewHolder onCreateViewHolder
                (@NonNull ViewGroup parent, int viewType) {
            View itemList = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_images, parent, false);

            return new AdapterImages.ImageViewHolder(itemList);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterImages.ImageViewHolder holder, int position) {

            String photo = photos.get(position);
            Uri photoUrl = Uri.parse(photo);

            Glide.with(context).load(photoUrl).into(holder.announceImage);

            if (photos.size() == 1){

                holder.btnPreviousImage.setVisibility(View.INVISIBLE);
                holder.btnNextImage.setVisibility(View.INVISIBLE);

            }else {

                if (position == 0){

                    holder.btnPreviousImage.setVisibility(View.INVISIBLE);
                    holder.btnNextImage.setVisibility(View.VISIBLE);

                }else if (position == (photos.size()-1)){

                    holder.btnPreviousImage.setVisibility(View.VISIBLE);
                    holder.btnNextImage.setVisibility(View.INVISIBLE);

                }else {

                    holder.btnPreviousImage.setVisibility(View.VISIBLE);
                    holder.btnNextImage.setVisibility(View.VISIBLE);
                }
            }

            holder.btnPreviousImage.setOnClickListener
                    (view -> recyclerImages.smoothScrollToPosition(position - 1));
            holder.btnNextImage.setOnClickListener
                    (view -> recyclerImages.smoothScrollToPosition(position + 1));
        }


        @Override
        public int getItemCount() {
            return photos.size();
        }
    }

}
