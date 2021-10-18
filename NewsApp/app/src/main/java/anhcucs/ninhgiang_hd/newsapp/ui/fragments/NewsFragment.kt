package anhcucs.ninhgiang_hd.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import anhcucs.ninhgiang_hd.newsapp.adapter.NewAdapter
import anhcucs.ninhgiang_hd.newsapp.databinding.FragmentNewsBinding
import anhcucs.ninhgiang_hd.newsapp.ui.MainActivity
import anhcucs.ninhgiang_hd.newsapp.utils.Constant.QUERY_PAGE_SIZE
import anhcucs.ninhgiang_hd.newsapp.utils.Resources
import anhcucs.ninhgiang_hd.newsapp.viewmodel.NewViewModel


class NewsFragment : BaseFragment<FragmentNewsBinding>() {
    private lateinit var newViewModel: NewViewModel

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private val newAdapter by lazy {
        NewAdapter()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentNewsBinding {
        return FragmentNewsBinding.inflate(inflater, container, false)
    }


    override  fun initView() {
        super.initView()
        newViewModel = (activity as MainActivity).newViewModel
        binding.rcvNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newAdapter
            addOnScrollListener(this@NewsFragment.scrollListener)
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun initAction() {
        super.initAction()
        binding.btnRetry.setOnClickListener {
            newViewModel.getBreakingList()
            hideCheckInternet()
        }
        newAdapter.onItemClick = {
            findNavController().navigate(
                NewsFragmentDirections.actionNewsFragmentToArticleFragment(
                    it
                )
            )
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        newViewModel.breakingList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    hideCheckInternet()
                    response.data?.let { newsResponse ->
                        newAdapter.setData(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = newViewModel.pageBreaking == totalPages
                        if (isLastPage) {
                            binding.rcvNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                    hideCheckInternet()
                }
                is Resources.Error -> {
                    hideProgressBar()
                    showCheckInternet()
                }
            }

        }
    }

    private fun hideCheckInternet() {
        binding.apply {
            noInternet.visibility = View.INVISIBLE
            tvNoInternet.visibility = View.INVISIBLE
            btnRetry.visibility = View.INVISIBLE
            rcvNews.visibility = View.VISIBLE
        }
    }

    private fun showCheckInternet() {
        binding.apply {
            noInternet.visibility = View.VISIBLE
            tvNoInternet.visibility = View.VISIBLE
            btnRetry.visibility = View.VISIBLE
            rcvNews.visibility = View.INVISIBLE
        }
    }

    private fun hideProgressBar() {
        binding.loadingProgress.visibility = View.INVISIBLE
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.loadingProgress.visibility = View.VISIBLE
        binding.paginationProgressBar.visibility = View.VISIBLE
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newViewModel.getBreakingList()
                isScrolling = false
            }
        }
    }

}