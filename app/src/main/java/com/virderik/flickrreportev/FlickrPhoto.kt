package com.virderik.flickrreportev

import java.util.ArrayList

class FlickrPhoto {
    // The outermost wrapper for the api response
    data class PhotosSearchResponse(
            val photos: FlickrMetaData
    )

    data class FlickrMetaData(
            val page: Int,
            val photo: ArrayList<FlickrPhotoDetail>
    )

    data class FlickrPhotoDetail(
            val id: String,
            val owner: String,
            val secret: String,
            val server: String,
            val farm: Int,
            val title: String
    )
}