package com.virderik.flickrreportev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FlickrViewModel viewModel;
    private PhotoRecyclerViewAdapter viewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "===start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photosearch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.photosRecyclerView);
        initRecyclerView();
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(MainActivity.this).get(FlickrViewModel.class);


        SearchView searchTags = findViewById(R.id.searchTags);
        searchTags.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                     new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //run initial Retrofit API through ViewModel
                            try {
                                viewModel.init(query);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // do onPostExecute
                                    viewModel.getPhotos().observe(MainActivity.this, userListUpdateObserver);
                                    viewAdapter = new PhotoRecyclerViewAdapter();
                                    recyclerView.setAdapter(viewAdapter);
                                }
                            });
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //Listens to any changes within ViewModel
    Observer<ArrayList<PhotoUrl.Photo>> userListUpdateObserver = new Observer<ArrayList<PhotoUrl.Photo>>() {
        @Override
        public void onChanged(ArrayList<PhotoUrl.Photo> photoArrayList) {
            viewAdapter.updatePhotos(photoArrayList);
        }
    };


    public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.CardListHolder> {

        private ArrayList<PhotoUrl.Photo> cardPhotos;

        PhotoRecyclerViewAdapter() {
            this.cardPhotos = new ArrayList<PhotoUrl.Photo>();
        }

        @Override
        public CardListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photodetails, parent, false);
            return new CardListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardListHolder holder, int position) {
            holder.bind(cardPhotos.get(position));
        }


        @Override
        public int getItemCount() {
            return cardPhotos.size();
        }

        public void updatePhotos(ArrayList<PhotoUrl.Photo> photoList) {
            this.cardPhotos.clear();
            this.cardPhotos = photoList;
            notifyDataSetChanged();
        }

        class CardListHolder extends RecyclerView.ViewHolder {
            CardView display;
            ImageView flickrImage;
            TextView title;

            public CardListHolder(@NonNull View itemView) {
                super(itemView);
                display = (CardView) itemView.findViewById(R.id.thumbCard);
                flickrImage = (ImageView) itemView.findViewById(R.id.thumbImage);
                title = (TextView) itemView.findViewById(R.id.thumbTitle);

            }

            void bind(PhotoUrl.Photo photo) {
                Picasso.get().load(photo.getUrl()).into(flickrImage);
                title.setText(photo.getTitle());

                display.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);

                        intent.setDataAndType(Uri.parse(photo.getUrl().replace("_m", "_z")), "image/*");
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
}
