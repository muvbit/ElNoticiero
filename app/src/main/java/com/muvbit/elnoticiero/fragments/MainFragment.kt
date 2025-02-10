package com.muvbit.elnoticiero.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        binding.imgLevanteEMV.setOnClickListener {
            val newsUrl = "https://www.levante-emv.com/"
            val action = MainFragmentDirections.actionMainFragmentToNewsFragment(newsUrl,R.drawable.levanteemv_logo)
            findNavController().navigate(action)
        }
    }
    private fun getCurrentDate(): String {
        val calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfMonthWithSuffix = when (dayOfMonth) {
            1, 21, 31 -> "${dayOfMonth}st"
            2, 22 -> "${dayOfMonth}nd"
            3, 23 -> "${dayOfMonth}rd"
            else -> "${dayOfMonth}th"
        }
        val formattedDate = dateFormat.format(calendar.time)
        return formattedDate.replaceFirst(Regex("\\d{1,2}"), dayOfMonthWithSuffix)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}