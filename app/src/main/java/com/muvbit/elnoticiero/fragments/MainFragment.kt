package com.muvbit.elnoticiero.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener la actividad principal
        val activityMain=requireActivity() as MainActivity
        val activityMainBinding=activityMain.binding

        activityMainBinding.bottomNav.menu.clear()
        activityMainBinding.bottomNav.inflateMenu(R.menu.bottom_main_menu)
        activityMainBinding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myFavorites -> {
                    findNavController().navigate(R.id.action_mainFragment_to_favoriteNewsFragment)
                    true
                }
                else -> false
            }
        }




        binding.tvToday.text=getCurrentDate()

        binding.imgLaRazon.setOnClickListener {
            val newsUrl = "https://www.larazon.es/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl, R.drawable.larazon_logo)
            findNavController().navigate(action)
        }
        binding.imgABC.setOnClickListener {
            val newsUrl = "https://www.abc.es/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl, R.drawable.abc_logo)
            findNavController().navigate(action)
        }
        binding.imgElMundo.setOnClickListener {
            val newsUrl = "https://www.elmundo.es/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl, R.drawable.elmundo_logo)
            findNavController().navigate(action)
        }
        binding.imgElPais.setOnClickListener {
            val newsUrl = "https://elpais.com/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl,R.drawable.elpais_logo)
            findNavController().navigate(action)
        }
        binding.imgLaVanguardia.setOnClickListener {
            val newsUrl = "https://www.lavanguardia.com/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl,R.drawable.lavanguardia_logo)
            findNavController().navigate(action)
        }
        binding.imgElEspanol.setOnClickListener {
            val newsUrl = "https://www.elespanol.com/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl,R.drawable.elespanol_logo)
            findNavController().navigate(action)
        }
    }
    private fun getCurrentDate(): String {
        val calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        return formattedDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}