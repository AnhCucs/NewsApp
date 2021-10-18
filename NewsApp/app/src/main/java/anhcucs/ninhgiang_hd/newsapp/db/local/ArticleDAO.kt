package anhcucs.ninhgiang_hd.newsapp.db.local

import androidx.lifecycle.LiveData
import androidx.room.*
import anhcucs.ninhgiang_hd.newsapp.db.model.Article

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDB(article: Article): Long

    @Query("Select * From article_table")
     fun getAllArticle(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)


}