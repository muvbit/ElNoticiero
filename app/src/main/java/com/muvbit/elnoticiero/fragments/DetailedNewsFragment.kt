package com.muvbit.elnoticiero.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.database.AppDatabase
import com.muvbit.elnoticiero.database.FavoriteNewsRepository
import com.muvbit.elnoticiero.databinding.FragmentDetailedNewsBinding
import com.muvbit.elnoticiero.model.FavoriteNews
import kotlinx.coroutines.launch
import java.lang.Long

class DetailedNewsFragment : Fragment() {

    private lateinit var binding: FragmentDetailedNewsBinding
    private val args: DetailedNewsFragmentArgs by navArgs()
    private lateinit var favoriteNewsRepository: FavoriteNewsRepository
    private var isFavorite: Boolean = false
    private lateinit var favoriteNews: FavoriteNews

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
        val db = AppDatabase.getDatabase(requireContext())
        // Get the DAO instance
        val favoriteNewsDao = db.favoriteNewsDao()
        // Get the repository instance
        favoriteNewsRepository = FavoriteNewsRepository(favoriteNewsDao)

        // Set the news data to the layout
        binding.tvArticleTitle.text = args.news.title
        binding.tvAuthorName.text = args.news.authors
        binding.tvCategory.text = args.news.category
        binding.tvArticleBody.text = args.news.text
        binding.tvDate.text = args.news.publishedAt
        Glide.with(this).load(args.news.urlImage).into(binding.articleImage)

        //Create the favorite news object
        favoriteNews = FavoriteNews(
            0,
            args.news.idNews,
            args.news.title,
            args.news.summary,
            args.news.text,
            args.news.authors,
            args.news.category,
            args.news.publishedAt,
            args.news.urlImage,
        )

        // Check if the news is already a favorite
        lifecycleScope.launch {
            val existingFavoriteNews: FavoriteNews? = favoriteNewsRepository.getFavoriteNewsByIdNews(args.news.idNews)
            isFavorite = existingFavoriteNews != null
            updateFavoriteIcon()
        }

        // Set the click listener for the favorite icon
        binding.imgFavorite.setOnClickListener {
            lifecycleScope.launch {
                if (isFavorite) {
                    // Remove from favorites
                    favoriteNewsRepository.delete(favoriteNews)
                    isFavorite = false
                } else {
                    // Add to favorites
                    favoriteNewsRepository.insert(favoriteNews)
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