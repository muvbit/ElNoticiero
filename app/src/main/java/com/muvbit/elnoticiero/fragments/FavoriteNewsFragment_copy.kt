package com.muvbit.elnoticiero.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muvbit.elnoticiero.adapters.FavoriteNewsAdapter
import com.muvbit.elnoticiero.adapters.NewsAdapter
import com.muvbit.elnoticiero.database.AppDatabase
import com.muvbit.elnoticiero.database.FavoriteNewsRepository
import com.muvbit.elnoticiero.databinding.FragmentFavoriteNewsBinding
import com.muvbit.elnoticiero.model.FavoriteNews
import com.muvbit.elnoticiero.model.News
import kotlinx.coroutines.launch

class FavoriteNewsFragment_copy : Fragment() {

    private var _binding: FragmentFavoriteNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: FavoriteNewsAdapter
    private lateinit var favoriteNewsRepository: FavoriteNewsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appDatabase = AppDatabase.getDatabase(requireContext())
        favoriteNewsRepository = FavoriteNewsRepository(appDatabase.favoriteNewsDao())

        binding.favoriteNewsRecyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            favoriteNewsRepository.getAllFavoriteNews().collect { favoriteNewsList ->
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
        _binding = null
    }
}