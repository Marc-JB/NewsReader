package nl.bouwman.marc.news.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import nl.bouwman.marc.news.ui.articles.ArticleAdapter
import nl.bouwman.marc.news.ui.R
import nl.bouwman.marc.news.ui.databinding.ActivityAccountOverviewBinding
import nl.bouwman.marc.news.ui.utils.defaultEncryptedPreferences
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountOverviewBinding

    private lateinit var adapter: ArticleAdapter

    private val viewModel by viewModel<AccountOverviewViewModel>()

    private val preferences by lazy {
        applicationContext.defaultEncryptedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = ArticleAdapter(this, viewModel.articles, false)

        binding.content.recyclerView.setHasFixedSize(true)
        binding.content.recyclerView.adapter = adapter

        binding.content.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadArticles()
        }

        binding.content.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.content.username.text = preferences.getString(
            AccountManagerImpl.SETTINGS_USERNAME,
            null
        ) ?: getString(R.string.na)

        viewModel.isLoading.observe(this) {
            binding.content.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.articles.observe(this) {
            adapter.notifyDataSetChanged()
        }

        viewModel.isLoggedIn.observe(this) {
            if(!it) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}