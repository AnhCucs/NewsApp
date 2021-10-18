package anhcucs.ninhgiang_hd.newsapp.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anhcucs.ninhgiang_hd.newsapp.ApplicationContext
import anhcucs.ninhgiang_hd.newsapp.db.model.Article
import anhcucs.ninhgiang_hd.newsapp.db.model.NewsResponse
import anhcucs.ninhgiang_hd.newsapp.repository.NewRepository
import anhcucs.ninhgiang_hd.newsapp.utils.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewViewModel(private val newRepository: NewRepository) : ViewModel() {

    private val _breakingList = MutableLiveData<Resources<NewsResponse>>()
    val breakingList: LiveData<Resources<NewsResponse>>
        get() = _breakingList
    private var breakingResponse: NewsResponse? = null
    var pageBreaking = 1

    private val _searchList = MutableLiveData<Resources<NewsResponse>>()
    val searchList: LiveData<Resources<NewsResponse>> get() = _searchList
    private var searchResponse: NewsResponse? = null
    var pageSearch = 1
    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null


    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    init {
        getBreakingList()
    }

    fun insertToDB(article: Article) {
        viewModelScope.launch {
            newRepository.insertToDB(article)
        }
    }

    fun deleteToDB(article: Article) = viewModelScope.launch {
        newRepository.deleteToDB(article)
    }

     fun getAllToDB() =
         newRepository.getAllToDB()



    fun checkDatabase(listArticle: List<Article>) {
        _isEmpty.value = listArticle.isEmpty()
    }

    fun getSearchList(queryString: String) = viewModelScope.launch {
        safeSearchList(queryString)
    }

    fun getBreakingList() = viewModelScope.launch(Dispatchers.IO) {
        safeBreakingList("us")
    }

    private suspend fun safeSearchList(searchQuery: String) {
        _searchList.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newRepository.searchNews(searchQuery, pageSearch)
                _searchList.postValue(handleSearch(response))
            } else {
                _searchList.postValue(Resources.Error("No Internet"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchList.postValue(Resources.Error("Network fail"))
                else -> _searchList.postValue(Resources.Error("Please Check Error"))
            }
        }

    }

    private fun handleSearch(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                pageSearch++
                if (searchResponse == null || newSearchQuery != oldSearchQuery) {
                    pageSearch = 1
                    oldSearchQuery = newSearchQuery
                    searchResponse = newsResponse
                } else {
                    val oldSearch = searchResponse?.articles
                    val newSearch = newsResponse.articles
                    oldSearch?.addAll(newSearch)
                }
                return Resources.Success(searchResponse ?: newsResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private suspend fun safeBreakingList(countryCode: String) {
        _breakingList.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newRepository.getBreakingNews(countryCode, pageBreaking)
                _breakingList.postValue(handleBreakingList(response))
            } else {
                _breakingList.postValue(Resources.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _breakingList.postValue(Resources.Error("Network Failure"))
                else -> _breakingList.postValue(Resources.Error("Conversion Error"))
            }
        }

    }

    private fun handleBreakingList(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newResult ->
                pageBreaking++
                if (breakingResponse == null) {
                    breakingResponse = newResult
                } else {
                    val oldArticle = breakingResponse?.articles
                    val newArticle = newResult.articles
                    oldArticle?.addAll(newArticle)

                }
                return Resources.Success(breakingResponse ?: newResult)
            }
        }
        return Resources.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            ApplicationContext.getInstance().baseContext.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}