package com.muvbit.elnoticiero.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.muvbit.elnoticiero.adapters.NewsAdapter
import com.muvbit.elnoticiero.databinding.FragmentNewsBinding
import com.muvbit.elnoticiero.model.News
import com.muvbit.elnoticiero.model.NewsApiData
import com.muvbit.elnoticiero.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    val args: NewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgNewsLogo.setImageResource(args.newsLogo)
        fetchNews()
    }

    private fun setupRecyclerView(newsList: List<News>) {
        Log.d("NewsFragment", "setupRecyclerView called with list size: ${newsList.size}") // Add this log
        newsAdapter = NewsAdapter(newsList)
        binding.newsRecyclerView.apply {
            adapter = newsAdapter
        }
    }

    private fun fetchNews() {

        val newsSource = args.newsUrl // RECOGEMOS LA URL DEL PERIODICO

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.apiService.getNews(
                    number = "10",
                    newsSources = newsSource,
                    earliestPublishDate = getCurrentDate(),
                    language = "es"
                )

                if (response.isSuccessful) {
                    val newsApiResponse = response.body()
                    Log.d("NewsFragment", "API Response: $newsApiResponse") // Verificar datos crudos

                    val newsList = newsApiResponse?.data?.let { data: List<NewsApiData> ->
                        data.map { newsApiData ->
                            mapNewsApiDataToNews(newsApiData)
                        }
                    } ?: emptyList()

                    Log.d("NewsFragment", "Mapped newsList size: ${newsList.size}") // Verificar tamaño de la lista

                    withContext(Dispatchers.Main) {
                        setupRecyclerView(newsList)
                    }

                } else {
                    Log.e("NewsFragment", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NewsFragment", "Exception: ${e.message}")
            }
        }
    }

    private fun getCurrentDate(): String { // PARA RECOGER LA FECHA ACTUAL, ASÍ LA INSERTAMOS EN EL CAMPO DEL GET PARA OBTENER LAS NOTICIAS DE HOY.
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun mapNewsApiDataToNews(newsApiData: NewsApiData): News {
        Log.d("NewsFragment", "Mapping news: ${newsApiData.title} - ${newsApiData.publishedAt}")
        return News(
            title = newsApiData.title ?: "Sin título",
            summary = newsApiData.summary ?: "Sin resumen",
            text = newsApiData.text ?: "Sin descripción",
            authors = newsApiData.author ?: "Desconocido",
            category = newsApiData.category,
            date = newsApiData.publishedAt ?: "Fecha desconocida",
            urlImage = newsApiData.imageUrl ?: ""
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}