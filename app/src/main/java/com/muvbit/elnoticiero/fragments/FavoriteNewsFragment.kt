package com.muvbit.elnoticiero.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.adapters.FavoriteNewsAdapter
import com.muvbit.elnoticiero.database.AppDatabase
import com.muvbit.elnoticiero.database.FavoriteNewsRepository
import com.muvbit.elnoticiero.databinding.FragmentFavoriteNewsBinding
import com.muvbit.elnoticiero.model.FavoriteNews
import com.muvbit.elnoticiero.model.News
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.text.clear

class FavoriteNewsFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteNewsBinding

    private lateinit var newsAdapter: FavoriteNewsAdapter
    private lateinit var favoriteNewsRepository: FavoriteNewsRepository
    private var favoriteNewsList: MutableList<FavoriteNews> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Obtener la actividad principal
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding

        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.inflateMenu(R.menu.bottom_favorite_menu)

        //BASE DE DATOS
        val appDatabase = AppDatabase.getDatabase(requireContext())
        favoriteNewsRepository = FavoriteNewsRepository(appDatabase.favoriteNewsDao())

        binding.favoriteNewsRecyclerView.layoutManager = LinearLayoutManager(context)
        /*
        lifecycleScope.launch {
            favoriteNewsRepository.getAllFavoriteNews().collect { favoriteNewsList ->
                val newsList = favoriteNewsList.map { mapFavoriteNewsToNews(it) }
                setupRecyclerView(newsList)
            }
        }

         */
        loadFavoriteNews()

        mainActivityBinding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favoriteDeleteAll -> {
                    lifecycleScope.launch {
                    favoriteNewsRepository.deleteAllFavoriteNews()
                        favoriteNewsList.clear()
                        loadFavoriteNews()
                    }
                    true
                }
                else -> false
            }
        }

    }
    private fun loadFavoriteNews() {
        lifecycleScope.launch {
            favoriteNewsRepository.getAllFavoriteNews().collect { favoriteNewsList ->
                this@FavoriteNewsFragment.favoriteNewsList.clear()
                this@FavoriteNewsFragment.favoriteNewsList.addAll(favoriteNewsList)
                val newsList = favoriteNewsList.map { mapFavoriteNewsToNews(it) }
                setupRecyclerView(newsList)
            }
        }
    }

    private fun setupRecyclerView(newsList: List<News>) {
        Log.d("FavoriteNewsFragment", "setupRecyclerView called with list size: ${newsList.size}")
        if (newsList.isNotEmpty()) {
            newsAdapter = FavoriteNewsAdapter(newsList)
            binding.favoriteNewsRecyclerView.apply {
                adapter = newsAdapter
            }
            binding.favoriteNewsCenterMessage.visibility = View.GONE
        } else {
            binding.favoriteNewsCenterMessage.visibility = View.VISIBLE
        }
    }

    private fun mapFavoriteNewsToNews(favoriteNews: FavoriteNews): News {
        return News(
            title = favoriteNews.title,
            summary = favoriteNews.summary,
            text = favoriteNews.text,
            authors = favoriteNews.authors,
            category = favoriteNews.category,
            publishedAt = favoriteNews.date,
            urlImage = favoriteNews.urlImage
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}