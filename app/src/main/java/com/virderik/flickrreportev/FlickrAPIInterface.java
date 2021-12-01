package com.virderik.flickrreportev;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FlickrAPIInterface {
    //Url for gathering FlickrImages, takes comma delimited tags
    @GET("?method=flickr.photos.search&per_page=25&format=json&nojsoncallback=1&api_key=1508443e49213ff84d566777dc211f2a")
    Call<FlickrPhoto.PhotosSearchResponse> getSearchResults(@Query("tags") String tags);
}
