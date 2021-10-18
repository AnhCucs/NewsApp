package anhcucs.ninhgiang_hd.newsapp.network

import anhcucs.ninhgiang_hd.newsapp.db.model.NewsResponse
import anhcucs.ninhgiang_hd.newsapp.utils.Constant.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiNewsService {
    @GET("v2/top-headlines")
    suspend fun getTopNews(
        @Query("country") countryCode: String = "us",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>


    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>
}