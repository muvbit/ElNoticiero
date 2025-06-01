package com.muvbit.elnoticiero.fragments.news

import android.content.SharedPreferences
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
import androidx.preference.PreferenceManager
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.adapters.NewsAdapter
import com.muvbit.elnoticiero.databinding.FragmentNewsBinding
import com.muvbit.elnoticiero.model.News
import com.muvbit.elnoticiero.model.NewsApiData
import com.muvbit.elnoticiero.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class NewsFragment : Fragment(), FilterDialogFragment.FilterDialogListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val fragmentJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + fragmentJob)

    private lateinit var newsAdapter: NewsAdapter
    val args: NewsFragmentArgs by navArgs()
    private var newsSource: String? = null
    private var isFilterDialogVisible = false
    lateinit var pref: SharedPreferences
    private lateinit var userNewsCountries: String
    private lateinit var userNewsLanguage: String
    private lateinit var userNumberNews: String
    private lateinit var userEarliestDate: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        pref=PreferenceManager.getDefaultSharedPreferences(requireActivity())


        // Los listPreference deben devolver un Set<String> y devolver los valores del String con comas
        val countriesSet = pref.getStringSet("newsCountries", setOf("es")) ?: setOf("es")
        userNewsCountries = countriesSet.joinToString(",") // Convertimos el Set a String
        val languageSet = pref.getStringSet("newsLanguage", setOf("es")) ?: setOf("es")
        userNewsLanguage = languageSet.joinToString(",") // Convertimos el Set a String

        userNumberNews=pref.getInt("newsNumber", 10).toString()
        userEarliestDate=pref.getString("newsDate", "td")?: "td"

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N) //COMPATIBLE CON LAS FECHAS
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgNewsLogo.setImageResource(args.newsLogo)
        newsSource = args.newsUrl

        // Obtener la actividad principal
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        // Borramos el menu del bottomNav y le agregamos el personalizado para este fragment
        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.inflateMenu(R.menu.bottom_news_menu)

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
            fechaInicio = getDate(userEarliestDate),
            fechaFinal = null,
            numeroNoticias = userNumberNews,
            pais = userNewsCountries,
            lengua = userNewsLanguage?:"es",
            coordenadas = null
        )
    }

    private fun setupRecyclerView(newsList: List<News>) {
        Log.d("NewsFragment", "setupRecyclerView called with list size: ${newsList.size}") // Add this log
        if (!newsList.isEmpty()) {
            newsAdapter = NewsAdapter(newsList)
            binding.newsCenterMessage.visibility=View.GONE
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
       uiScope.launch {
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
                    Log.d("NewsFragment", "API Response: $newsApiResponse") // Verificar datos

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
                binding.newsCenterMessage.text=getString(R.string.server_no_response)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDate(dateType: String): String? {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return when (dateType) {
            "td" -> dateFormat.format(calendar.time) // Today
            "lw" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1) // Subtract one week
                dateFormat.format(calendar.time)
            }
            "lm" -> {
                calendar.add(Calendar.MONTH, -1) // Subtract one month
                dateFormat.format(calendar.time)
            }
            "fg" -> null // No date filter
            else -> null // Handle invalid input (optional)
        }
    }

    private fun mapNewsApiDataToNews(newsApiData: NewsApiData): News {
        Log.d("NewsFragment", "Mapping news: ${newsApiData.title} - ${newsApiData.publishedAt}")
        return News(
            idNews = newsApiData.idNews ?: "",
            title = newsApiData.title ?: "Sin título",
            summary = newsApiData.summary ?: "Sin resumen",
            text = newsApiData.text ?: "Sin descripción",
            authors = newsApiData.author ?: "Desconocido",
            category = newsApiData.category,
            publishedAt = newsApiData.publishedAt ?: "Fecha desconocida",
            urlImage = newsApiData.imageUrl ?: ""
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentJob.cancel()
        _binding = null

    }
}