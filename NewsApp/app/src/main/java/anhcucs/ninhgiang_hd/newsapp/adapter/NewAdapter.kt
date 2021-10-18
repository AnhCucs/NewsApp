package anhcucs.ninhgiang_hd.newsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import anhcucs.ninhgiang_hd.newsapp.databinding.ItemArticleBinding
import anhcucs.ninhgiang_hd.newsapp.db.model.Article
import anhcucs.ninhgiang_hd.newsapp.utils.DiffUtilArticle
import anhcucs.ninhgiang_hd.newsapp.utils.getFirstLetterSource
import anhcucs.ninhgiang_hd.newsapp.utils.getProgressDrawable
import anhcucs.ninhgiang_hd.newsapp.utils.loadImage

class NewAdapter : RecyclerView.Adapter<NewAdapter.MyViewHolder>() {

    var onItemClick: ((Article) -> Unit) = {}
    var articles = emptyList<Article>()

    inner class MyViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemCLick: (Article) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemCLick(articles[adapterPosition])
            }
        }

        fun bind(article: Article) {
            binding.apply {
                ivArticleImage.loadImage(article.urlToImage, getProgressDrawable(itemView.context))
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
                tvTitle.text = article.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(binding = binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun setData(articleList: List<Article>) {
        val diffUtilArticle = DiffUtilArticle(articles, articleList)
        val resultArticle = DiffUtil.calculateDiff(diffUtilArticle)
        this.articles = articleList
        resultArticle.dispatchUpdatesTo(this)
    }
}