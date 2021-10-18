package anhcucs.ninhgiang_hd.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import anhcucs.ninhgiang_hd.newsapp.adapter.NewAdapter
import anhcucs.ninhgiang_hd.newsapp.databinding.FragmentBookmarksBinding
import anhcucs.ninhgiang_hd.newsapp.db.model.Article
import anhcucs.ninhgiang_hd.newsapp.ui.MainActivity
import anhcucs.ninhgiang_hd.newsapp.utils.SwipeToDelete
import anhcucs.ninhgiang_hd.newsapp.viewmodel.NewViewModel
import com.google.android.material.snackbar.Snackbar


class BookmarksFragment : BaseFragment<FragmentBookmarksBinding>() {

    private lateinit var newViewModel: NewViewModel
    private val newAdapter by lazy {
        NewAdapter()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentBookmarksBinding {
        return FragmentBookmarksBinding.inflate(inflater, container, false)
    }


    override  fun initView() {
        super.initView()
        newViewModel = (activity as MainActivity).newViewModel
        binding.rcvBookmark.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newAdapter
        }

        swipeItem(binding.rcvBookmark)
    }

    private fun checkShowDB(value: Boolean) {
        if (value) {
            binding.rcvBookmark.visibility = View.INVISIBLE
            binding.imgEmpty.visibility = View.VISIBLE
        } else {
            binding.rcvBookmark.visibility = View.VISIBLE
            binding.imgEmpty.visibility = View.INVISIBLE
        }
    }

    override fun initAction() {
        super.initAction()

        newAdapter.onItemClick = {
            findNavController().navigate(
                BookmarksFragmentDirections.actionBookmarksFragmentToArticleFragment(
                    it
                )
            )
        }
    }

    private fun swipeItem(recyclerView: RecyclerView) {
        val swipeItem = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newAdapter.articles[position]
                newViewModel.deleteToDB(article)
                newAdapter.notifyItemChanged(position)
                view?.let {
                    Snackbar.make(it, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                        setAction("Undo") {
                            newViewModel.insertToDB(article)
                        }
                        show()
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeItem)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun observeLiveData() {
        super.observeLiveData()
        newViewModel.getAllToDB().observe(viewLifecycleOwner) {
            newViewModel.checkDatabase(it)
            newAdapter.setData(it)
        }
        newViewModel.isEmpty.observe(viewLifecycleOwner) {
            checkShowDB(it)
        }
    }

}