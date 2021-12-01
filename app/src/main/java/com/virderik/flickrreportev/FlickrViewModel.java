package com.virderik.flickrreportev;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.virderik.flickrreportev.FlickrPhoto.FlickrPhotoDetail;
import com.virderik.flickrreportev.PhotoUrl.Photo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class FlickrViewModel extends ViewModel {
    private ArrayList<FlickrPhotoDetail> photoDetail;
    private MutableLiveData<ArrayList<Photo>> mutableData;
    private ArrayList<Photo> photoArrayList;
    private ArrayList<Photo> photoArrayListEmpty;

    public FlickrViewModel() {
        super();
    }

    public MutableLiveData<ArrayList<Photo>> getPhotos() {
        if (mutableData == null) {
            photoArrayList = new ArrayList<>();
            photoArrayListEmpty = new ArrayList<>();
            mutableData = new MutableLiveData<ArrayList<Photo>>();
        }
        for (int i = 0; photoDetail.size() > i; i++) {
            Photo photoUrl = new Photo(
                    photoDetail.get(i).getId(), getFlickrPhotoUrl(photoDetail.get(i)),
                    photoDetail.get(i).getTitle()
            );
            photoArrayList.add(photoUrl);
        }
        mutableData.postValue(photoArrayList);
        return mutableData;
    }

    public void init(String query) throws IOException {
        if (mutableData != null) {
            mutableData.postValue(photoArrayListEmpty);
        }
        FlickrAPIInterface apiInterface = GetPhotoData.retrofit.create(FlickrAPIInterface.class);
        Call<FlickrPhoto.PhotosSearchResponse> FlickrCall = apiInterface.getSearchResults(query);

        Response<FlickrPhoto.PhotosSearchResponse> response = FlickrCall.execute();

        if (response.isSuccessful()) {
            photoDetail = response.body().getPhotos().getPhoto();
        } else {
            Log.d(TAG, "===" + response.errorBody().string());
        }
    }

    //Builds the image url by appending the nested json data
    private static String getFlickrPhotoUrl(FlickrPhotoDetail photoDetail) {

        String SERVERID = photoDetail.getServer();
        String SECRET = photoDetail.getSecret();
        String ID = String.valueOf(photoDetail.getId());

        return "https://live.staticflickr.com/" +
                SERVERID + "/" + ID + "_" +
                SECRET + "_m" + ".jpg";
    }

    //Tried using clear observable, but UI would not refresh after first search
    public void clear(Observer<ArrayList<PhotoUrl.Photo>> observer) {
        if (mutableData != null) {
            mutableData.removeObserver(observer);
        }
    }
}
