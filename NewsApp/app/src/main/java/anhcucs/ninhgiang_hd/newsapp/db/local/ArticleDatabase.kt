package anhcucs.ninhgiang_hd.newsapp.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import anhcucs.ninhgiang_hd.newsapp.db.model.Article

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)


abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDAO(): ArticleDAO

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getDatabase(context: Context): ArticleDatabase {
            val temInstance = INSTANCE
            if (temInstance != null) {
                return temInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
