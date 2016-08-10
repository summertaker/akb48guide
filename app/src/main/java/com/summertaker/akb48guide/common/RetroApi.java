package com.summertaker.akb48guide.common;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetroApi {
    public static final String BASE_URL_AKB48_SHOP = "http://shopping.akb48-group.com/products/";

    /*@Headers({
            "Accept: application/vnd.yourapi.v1.full+json",
            "User-Agent: Your-App-Name"
    })*/

    // http://shopping.akb48-group.com/products/list.php?akb48&category_id=3
    @GET("list.php?akb48")
    Call<ResponseBody> getRawPhotoAkb48(@Header("User-Agent") String userAgent, @Query("category_id") String categoryId, @Query("pageno") String pageNo);

    @GET("list.php?ske48")
    Call<ResponseBody> getRawPhotoSke48(@Header("User-Agent") String userAgent, @Query("category_id") String categoryId, @Query("pageno") String pageNo);

    @GET("list.php?nmb48")
    Call<ResponseBody> getRawPhotoNmb48(@Header("User-Agent") String userAgent, @Query("category_id") String categoryId, @Query("pageno") String pageNo);

    @GET("list.php?hkt48")
    Call<ResponseBody> getRawPhotoHkt48(@Header("User-Agent") String userAgent, @Query("category_id") String categoryId, @Query("pageno") String pageNo);
}
