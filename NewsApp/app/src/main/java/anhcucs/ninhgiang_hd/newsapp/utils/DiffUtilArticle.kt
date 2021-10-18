package anhcucs.ninhgiang_hd.newsapp.utils

import androidx.recyclerview.widget.DiffUtil
import anhcucs.ninhgiang_hd.newsapp.db.model.Article

class DiffUtilArticle(
    private val oldItem: List<Article>,
    private val newItem: List<Article>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItem.size
    }

    override fun getNewListSize(): Int {
        return newItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition] === newItem[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition] == newItem[newItemPosition]
    }
}