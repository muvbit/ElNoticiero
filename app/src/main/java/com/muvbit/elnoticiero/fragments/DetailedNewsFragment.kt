package com.muvbit.elnoticiero.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.FragmentDetailedNewsBinding
import com.muvbit.elnoticiero.databinding.FragmentNewsBinding

class DetailedNewsFragment : Fragment() {

    private lateinit var binding: FragmentDetailedNewsBinding
    val args: DetailedNewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDetailedNewsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvArticleTitle.text=args.news.title
        binding.tvAuthorName.text=args.news.authors
        binding.tvCategory.text=args.news.category
        binding.tvArticleBody.text=args.news.text
        Glide.with(this).load(args.news.urlImage).into(binding.articleImage)


    }

}