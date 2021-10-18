package anhcucs.ninhgiang_hd.newsapp.db.model

data class NewsResponse(
    val articles: ArrayList<Article>,
    val status: String,
    val totalResults: Int
)