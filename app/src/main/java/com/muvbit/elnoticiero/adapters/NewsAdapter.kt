package com.muvbit.elnoticiero.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.databinding.NewsItemBinding
import com.muvbit.elnoticiero.fragments.NewsFragmentDirections
import com.muvbit.elnoticiero.model.News

class NewsAdapter(private var newsList: List<News>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentNews = newsList[position]
        holder.bind(currentNews)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun updateNewsList(newList: List<News>) {
        newsList = newList
        notifyDataSetChanged()
    }

    class NewsViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: News) {
            Log.d("NewsViewHolder", "bind called for news: ${news.title}")
            Log.d("NewsViewHolder", "Summary: ${news.summary}")
            Log.d("NewsViewHolder", "Image URL: ${news.urlImage}")
            Log.d("NewsViewHolder", "Date: ${news.publishedAt}")
            binding.newsTitle.text = news.title
            binding.newsSummary.text = news.summary
            binding.tvDate.text = news.publishedAt


            Glide.with(binding.root.context) //PARA CARGAR LA IMAGEN
                .load(news.urlImage)
                .into(binding.newsImage)

            binding.cardView.setOnClickListener {
                val action = NewsFragmentDirections.actionNewsFragmentToDetailedNewsFragment(news)
                binding.root.findNavController().navigate(action)
            }
        }

    }
}