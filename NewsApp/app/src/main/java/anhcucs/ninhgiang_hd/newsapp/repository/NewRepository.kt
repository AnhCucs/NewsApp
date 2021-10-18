package anhcucs.ninhgiang_hd.newsapp.repository

import anhcucs.ninhgiang_hd.newsapp.ApplicationContext
import anhcucs.ninhgiang_hd.newsapp.db.local.ArticleDatabase
import anhcucs.ninhgiang_hd.newsapp.db.model.Article
import anhcucs.ninhgiang_hd.newsapp.db.model.NewsResponse
import anhcucs.ninhgiang_hd.newsapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class NewRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val articleDatabase: ArticleDatabase = ArticleDatabase.getDatabase(ApplicationContext.getInstance().applicationContext)
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return withContext(ioDispatcher) {
            RetrofitInstance.apiNewsService.getTopNews(countryCode, pageNumber)
        }
    }

    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
        return withContext(ioDispatcher) {
            RetrofitInstance.apiNewsService.searchNews(searchQuery, pageNumber)
        }
    }

    fun getAllToDB() = articleDatabase.getArticleDAO().getAllArticle()

    suspend fun insertToDB(article: Article) {
        return withContext(ioDispatcher) {
            articleDatabase.getArticleDAO().insertToDB(article)
        }
    }

    suspend fun deleteToDB(article: Article) {
        return withContext(ioDispatcher) {
            articleDatabase.getArticleDAO().deleteArticle(article)
        }
    }
}