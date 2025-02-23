package com.muvbit.elnoticiero.fragments

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.R
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

class NewsFragment : Fragment(), FilterDialogFragment.FilterDialogListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    val args: NewsFragmentArgs by navArgs()
    private var newsSource: String? = null
    private var isFilterDialogVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N) //COMPATIBLE CON LAS FECHAS
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgNewsLogo.setImageResource(args.newsLogo)
        newsSource = args.newsUrl

        //Obtener la actividad principal
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding

        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.inflateMenu(R.menu.bottom_menu)

        mainActivityBinding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.filter -> {
                    showFilterDialog()
                    true
                }

                R.id.clear_filters -> {
                    fetchNews(newsSource = newsSource)
                    true
                }

                else -> false
            }
        }
        fetchNews(
            newsSource = newsSource,
            fechaInicio = getCurrentDate(),
            fechaFinal = null,
            numeroNoticias = "10",
            pais = null,
            lengua = "es",
            coordenadas = null
        )
    }

    private fun setupRecyclerView(newsList: List<News>) {
        Log.d("NewsFragment", "setupRecyclerView called with list size: ${newsList.size}") // Add this log
        if (!newsList.isEmpty()) {
            newsAdapter = NewsAdapter(newsList)
            binding.newsRecyclerView.apply {
                adapter = newsAdapter
            }
        } else {
            binding.newsCenterMessage.text=getString(R.string.no_news_found)
            binding.imgNoNews.visibility=View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fetchNews(
        newsSource: String? = null,
        fechaInicio: String? = null,
        fechaFinal: String? = null,
        numeroNoticias: String? = null,
        pais: String? = null,
        lengua: String? = null,
        coordenadas: String? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.apiService.getNews(
                    number = numeroNoticias,
                    newsSources = newsSource,
                    earliestPublishDate = fechaInicio,
                    latestPublishDate = fechaFinal,
                    language = lengua,
                    sourceCountries = pais,
                    locationFilter = coordenadas
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

    @RequiresApi(Build.VERSION_CODES.N)
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
            publishedAt = newsApiData.publishedAt ?: "Fecha desconocida",
            urlImage = newsApiData.imageUrl ?: ""
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFilterDialog() {
        if (!isFilterDialogVisible) {
            isFilterDialogVisible = true
            val dialog = FilterDialogFragment()
            dialog.setListener(this)
            dialog.show(childFragmentManager, "FilterDialogFragment")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onFiltersApplied(
        startDate: String?,
        endDate: String?,
        newsNumber: String?,
        country: String?,
        language: String?,
        coordinates: String?
    ) {
        isFilterDialogVisible = false
        fetchNews(
            newsSource = newsSource,
            fechaInicio = startDate,
            fechaFinal = endDate,
            numeroNoticias = newsNumber,
            pais = country,
            lengua = language,
            coordenadas = coordinates
        )
    }

    override fun onDialogDismissed() {
        isFilterDialogVisible = false
    }
}