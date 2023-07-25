package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.imageUrl
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ErrorType
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                        imageUrl = post.attachment?.url
                    }
                )

                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onViewPhoto(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply {
                        imageUrl = post.attachment?.url
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            when(state.error) {
                ErrorType.LOAD ->
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                        .show()
                ErrorType.REMOVE ->
                    Snackbar.make(binding.root, R.string.error_remove, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok) { }
                        .show()
                ErrorType.SAVE ->
                    Snackbar.make(binding.root, R.string.error_saving, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok) { }
                        .show()
                ErrorType.LIKE ->
                    Snackbar.make(binding.root, R.string.error_like, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok) { }
                        .show()
                ErrorType.DISLIKE ->
                    Snackbar.make(binding.root, R.string.error_unlike, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok) { }
                        .show()
                else -> {}
            }
        }
        binding.newPostButton.setOnClickListener {
            viewModel.updateAll()
            binding.newPostButton.visibility = View.GONE
            binding.list.smoothScrollToPosition(0)
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            binding.newPostButton.visibility = View.VISIBLE
        }

        viewModel.addedPost.observe(viewLifecycleOwner) {
            binding.list.smoothScrollToPosition(0)
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}
