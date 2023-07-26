package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onViewPhoto(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            PostViewHolder {
        val binding =
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position:
    Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content


            // в адаптере
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"


            if (post.attachment == null ||
                post.attachment!!.url.isEmpty()){
                attachment.visibility = View.GONE
                attachmentImage.visibility = View.GONE
            } else{

                attachmentImage.visibility = View.VISIBLE
                if (post.attachment!!.type == AttachmentType.IMAGE) {
                    attachment.visibility = View.GONE
                }  else{
                    attachment.visibility = View.VISIBLE
                }
                val url =
                    "http://10.0.2.2:9999/media/${post.attachment!!.url}"

                Glide.with(binding.attachmentImage)
                    .load(url)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .into(binding.attachmentImage)
            }
            if (post.authorAvatar.isNotEmpty()) {
                val url =
                    "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
                val options = RequestOptions()
                options.circleCrop()


                Glide.with(binding.avatar)
                    .load(url)
                    .apply(options)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .into(binding.avatar)
            }

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            attachmentImage.setOnClickListener {
                onInteractionListener.onViewPhoto(post)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
                like.isChecked = post.likedByMe
                like.text = "${post.likes}"
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean
    {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post):
            Boolean {
        return oldItem == newItem
    }
}
