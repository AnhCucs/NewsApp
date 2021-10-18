package anhcucs.ninhgiang_hd.newsapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import anhcucs.ninhgiang_hd.newsapp.R
import anhcucs.ninhgiang_hd.newsapp.repository.NewRepository
import anhcucs.ninhgiang_hd.newsapp.viewmodel.NewViewModel
import anhcucs.ninhgiang_hd.newsapp.viewmodel.NewViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

// API KEY = 75c31d7c6b994338bb8e926487936680
class MainActivity : AppCompatActivity() {

    lateinit var newViewModel: NewViewModel
    private lateinit var navController: NavController
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavView)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.newsFragment,
                R.id.bookmarksFragment,
                R.id.searchFragment
            )
        )
        bottomNavigationView?.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupViewModel()
    }

    private fun setupViewModel() {
        val newsRepository = NewRepository()
        val viewModelProviderFactory = NewViewModelFactory(newsRepository)
        newViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewViewModel::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()|| super.onSupportNavigateUp()
    }

}