package com.muvbit.elnoticiero.fragments.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.database.NewsDatabase
import com.muvbit.elnoticiero.database.NewsRepository
import com.muvbit.elnoticiero.databinding.FragmentDetailedFavoriteNewsBinding
import com.muvbit.elnoticiero.databinding.FragmentDetailedNewsBinding
import com.muvbit.elnoticiero.databinding.FragmentFavoriteNewsBinding
import kotlinx.coroutines.launch

class DetailedFavoriteNewsFragment : Fragment() {

    private lateinit var binding: FragmentDetailedFavoriteNewsBinding
    val args: DetailedFavoriteNewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailedFavoriteNewsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val news = args.news
        val navController= Navigation.findNavController(requireView())

        // Get the database instance
        val db = NewsDatabase.getDatabase(requireContext())
        // Get the DAO instance
        val favoriteNewsDao = db.NewsDao()
        // Get the repository instance
        var favoriteNewsRepository = NewsRepository(favoriteNewsDao)

        //Obtener la actividad principal para acceder al BottomNav
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.inflateMenu(R.menu.bottom_favorite_detailed_menu)

        binding.tvArticleTitle.text = args.news.title
        binding.tvAuthorName.text = args.news.authors
        binding.tvCategory.text = args.news.category
        binding.tvArticleBody.text = args.news.text
        binding.tvDate.text = args.news.publishedAt
        Glide.with(this).load(args.news.urlImage).into(binding.articleImage)

        mainActivityBinding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favoriteDelete -> {
                    lifecycleScope.launch {
                        AlertDialog.Builder(this@DetailedFavoriteNewsFragment.requireContext()).setMessage(R.string.areYouSureDeleteFromFavorites)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                lifecycleScope.launch {
                                    favoriteNewsRepository.deleteByIdNews(news.idNews?: "")
                                    Log.d("DetailedNewsFragment", "News deleted from favorites: ${news.title}")
                                    Snackbar.make(binding.root, getString(R.string.news_deleted_from_favorites), Snackbar.LENGTH_SHORT).show()}
                                     navController.popBackStack() //Atrás en el navegation graph y así cerramos el fragment.
                            }
                            .setNegativeButton("No", null).show()
                    }
                    true
                }

                else -> false

            }


        }
    }
}

