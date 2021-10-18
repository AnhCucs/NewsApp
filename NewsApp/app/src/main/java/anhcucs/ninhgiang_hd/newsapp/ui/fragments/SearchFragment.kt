package anhcucs.ninhgiang_hd.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import anhcucs.ninhgiang_hd.newsapp.adapter.NewAdapter
import anhcucs.ninhgiang_hd.newsapp.databinding.FragmentSearchBinding
import anhcucs.ninhgiang_hd.newsapp.ui.MainActivity
import anhcucs.ninhgiang_hd.newsapp.utils.Constant
import anhcucs.ninhgiang_hd.newsapp.utils.Resources
import anhcucs.ninhgiang_hd.newsapp.viewmodel.NewViewModel
import kotlinx.coroutines.*


class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private lateinit var newViewModel: NewViewModel
    private val newAdapter by lazy {
        NewAdapter()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override  fun initView() {
        super.initView()
        newViewModel = (activity as MainActivity).newViewModel

        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newAdapter
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    newViewModel.getSearchList(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    newViewModel.getSearchList(newText)
                }
                return true
            }

        })

    }

    override fun observeLiveData() {
        super.observeLiveData()
        newViewModel.searchList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newAdapter.setData(it.articles.toList())
                        val totalPages = it.totalResults / Constant.QUERY_PAGE_SIZE + 2
                        isLastPage = newViewModel.pageBreaking == totalPages
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("TAG", "An error occured: $message")
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }

        }
    }

    private fun hideProgressBar() {
        binding.apply {
            loadingSearchProgress.visibility = View.INVISIBLE
        }
    }

    private fun showProgressBar() {
        binding.apply {
            loadingSearchProgress.visibility = View.VISIBLE
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constant.QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newViewModel.getSearchList(binding.searchView.toString())
                isScrolling = false
            }
        }
    }
}