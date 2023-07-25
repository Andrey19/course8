package ru.netology.nmedia.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class PhotoFragment : Fragment() {

    companion object {
        var Bundle.imageUrl: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(inflater, container,
            false)

        val url = "http://10.0.2.2:9999/media/${arguments?.imageUrl}"
        binding.photoContainer.setBackgroundColor(Color.rgb(0, 0, 0))
        binding.photo.setBackgroundColor(Color.rgb(0, 0, 0))

        Glide.with(binding.photo)
            .load(url)
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.ic_error_100dp)
            .timeout(10_000)
            .into(binding.photo)

        return binding.root
    }
}
