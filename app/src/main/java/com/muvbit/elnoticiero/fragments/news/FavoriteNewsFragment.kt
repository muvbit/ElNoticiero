package com.muvbit.elnoticiero.fragments.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.adapters.FavoriteNewsAdapter
import com.muvbit.elnoticiero.database.NewsDatabase
import com.muvbit.elnoticiero.database.NewsRepository
import com.muvbit.elnoticiero.databinding.FragmentFavoriteNewsBinding
import com.muvbit.elnoticiero.model.News
import kotlinx.coroutines.launch

class FavoriteNewsFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteNewsBinding
    private lateinit var newsAdapter: FavoriteNewsAdapter
    private lateinit var favoriteNewsRepository: NewsRepository
    private var favoriteNewsList: MutableList<News> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the main activity
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding

        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.inflateMenu(R.menu.bottom_favorite_menu)

        // Database
        val appDatabase = NewsDatabase.getDatabase(requireContext())
        favoriteNewsRepository = NewsRepository(appDatabase.NewsDao())

        binding.favoriteNewsRecyclerView.layoutManager = LinearLayoutManager(context)
        newsAdapter = FavoriteNewsAdapter(favoriteNewsList)
        binding.favoriteNewsRecyclerView.adapter = newsAdapter

        loadFavoriteNews()

        mainActivityBinding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favoriteDeleteAll -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(getString(R.string.areYouSureDeleteAllFavorites))
                        .setPositiveButton(R.string.yes) { _, _ ->
                            lifecycleScope.launch {
                                favoriteNewsRepository.deleteAllFavoriteNews()
                                loadFavoriteNews()
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.all_news_deleted_from_favorites),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFavoriteNews() {
        lifecycleScope.launch {
            val newFavoriteNewsList = favoriteNewsRepository.getAllFavoriteNews().toMutableList()
            setupRecyclerView(newFavoriteNewsList)
        }
    }

    private fun setupRecyclerView(newFavoriteNewsList: MutableList<News>) {
        Log.d("FavoriteNewsFragment", "setupRecyclerView called with list size: ${newFavoriteNewsList.size}")
        favoriteNewsList.clear()
        favoriteNewsList.addAll(newFavoriteNewsList)
        newsAdapter.updateNewsList(favoriteNewsList)
        if (favoriteNewsList.isNotEmpty()) {
            binding.favoriteNewsCenterMessage.visibility = View.GONE
        } else {
            binding.favoriteNewsCenterMessage.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}