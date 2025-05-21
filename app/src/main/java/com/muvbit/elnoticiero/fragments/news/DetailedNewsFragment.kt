package com.muvbit.elnoticiero.fragments.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.database.NewsDatabase
import com.muvbit.elnoticiero.database.NewsRepository
import com.muvbit.elnoticiero.databinding.FragmentDetailedNewsBinding
import com.muvbit.elnoticiero.model.News
import kotlinx.coroutines.launch


class DetailedNewsFragment : Fragment() {

    private lateinit var binding: FragmentDetailedNewsBinding
    private val args: DetailedNewsFragmentArgs by navArgs()
    private lateinit var favoriteNewsRepository: NewsRepository
    private var isFavorite: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the main activity to access the BottomNav
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        mainActivityBinding.bottomNav.menu.clear()

        // Get the database instance
        val db = NewsDatabase.getDatabase(requireContext())
        // Get the DAO instance
        val favoriteNewsDao = db.NewsDao()
        // Get the repository instance
        favoriteNewsRepository = NewsRepository(favoriteNewsDao)


        val news = args.news
        // Set the news data to the layout
        binding.tvArticleTitle.text = news.title
        binding.tvAuthorName.text = news.authors
        binding.tvCategory.text = news.category
        binding.tvArticleBody.text = news.text
        binding.tvDate.text = news.publishedAt
        Glide.with(this).load(news.urlImage).into(binding.articleImage)




        // Check if the news is already a favorite
        lifecycleScope.launch {
            val existingFavoriteNews: News? = favoriteNewsRepository.getFavoriteNewsByIdNews(args.news.idNews)
            isFavorite = existingFavoriteNews != null
            updateFavoriteIcon()
        }

        // Set the click listener for the favorite icon
        binding.imgFavorite.setOnClickListener {
            lifecycleScope.launch {
                if (isFavorite) {
                    // ELIMIAR DE FAVORITOS
                    AlertDialog.Builder(this@DetailedNewsFragment.requireContext()).setMessage(R.string.areYouSureDeleteFromFavorites)
                        .setPositiveButton("Sí") { _, _ ->
                            lifecycleScope.launch {
                             favoriteNewsRepository.deleteByIdNews(news.idNews?: "")
                            Log.d("DetailedNewsFragment", "News deleted from favorites: ${news.title}")
                            isFavorite = false
                            Snackbar.make(binding.root, getString(R.string.news_deleted_from_favorites), Snackbar.LENGTH_SHORT).show()}
                        }
                            .setNegativeButton("No", null).show()
                } else {
                    // Add to favorites
                    favoriteNewsRepository.insert(news)
                    Log.d("DetailedNewsFragment", "News added to favorites: ${news.title}")
                    Snackbar.make(binding.root, getString(R.string.news_added_to_favorites), Snackbar.LENGTH_SHORT).show()
                    isFavorite = true
                }
                updateFavoriteIcon()
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.imgFavorite.setImageResource(R.drawable.favorite_icon)
        } else {
            binding.imgFavorite.setImageResource(R.drawable.unfavorite_icon)
        }
    }
}