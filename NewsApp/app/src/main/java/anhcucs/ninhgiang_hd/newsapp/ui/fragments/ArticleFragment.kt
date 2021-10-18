package anhcucs.ninhgiang_hd.newsapp.ui.fragments

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import anhcucs.ninhgiang_hd.newsapp.databinding.FragmentArticleBinding
import anhcucs.ninhgiang_hd.newsapp.db.model.Article
import anhcucs.ninhgiang_hd.newsapp.ui.MainActivity
import anhcucs.ninhgiang_hd.newsapp.utils.getFirstLetterSource
import anhcucs.ninhgiang_hd.newsapp.viewmodel.NewViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : BaseFragment<FragmentArticleBinding>() {
    private val args by navArgs<ArticleFragmentArgs>()
    lateinit var viewModel: NewViewModel
    private var result: Article? = null
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentArticleBinding {
        return FragmentArticleBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initData() {
        super.initData()
        result = args.article
        viewModel = (activity as MainActivity).newViewModel

        binding.webView.apply {
            webViewClient = WebViewClient()
            result!!.url?.let { loadUrl(it) }
            settings.userAgentString = "Android"
        }

        setupProgressBar()
        if (result?.id == null) {
            binding.fab.visibility = View.VISIBLE
            binding.webView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY && scrollY > 0) {
                    binding.fab.hide()
                }
                if (scrollY < oldScrollY) {
                    binding.fab.show()
                }
            }
        } else {
            binding.fab.visibility = View.INVISIBLE
        }
//        binding.tvSourceArticle.text = result?.source?.name
        binding.tvPublishedAtArticle.text = result?.publishedAt?.toString()
//        binding.tvSourceLetterArticle.text =
//            result?.source?.let { getFirstLetterSource(it.name).toString() }


    }

    override fun initAction() {
        super.initAction()
        binding.fab.setOnClickListener {
            result?.let { it1 -> viewModel.insertToDB(it1) }
            view?.let { it1 -> Snackbar.make(it1, "Article saved successfully", Snackbar.LENGTH_SHORT).show() }
        }
    }
    private fun setupProgressBar() {
        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
               binding.loadingWebView.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                try {
//                    binding.loadingWebView.visibility = View.INVISIBLE
                } catch (e: NullPointerException) {
                    Log.d("TAG", e.message.toString())
                }
            }
        }
    }


}