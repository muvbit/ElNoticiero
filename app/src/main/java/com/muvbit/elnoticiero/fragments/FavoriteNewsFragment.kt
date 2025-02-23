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


        //Obtener la actividad principal
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding

        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.inflateMenu(R.menu.bottom_favorite_menu)

        //BASE DE DATOS
        val appDatabase = NewsDatabase.getDatabase(requireContext())
        favoriteNewsRepository = NewsRepository(appDatabase.NewsDao())

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
            favoriteNewsList = favoriteNewsRepository.getAllFavoriteNews().toMutableList()
            setupRecyclerView(favoriteNewsList)
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


    override fun onDestroyView() {
        super.onDestroyView()
    }
}