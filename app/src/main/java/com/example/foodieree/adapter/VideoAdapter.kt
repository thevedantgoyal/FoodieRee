package com.example.viewpager2withexoplayer

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import com.example.foodieree.R
import com.example.foodieree.databinding.ItemReelBinding
import com.example.foodieree.model.ExoPlayerItem
import com.example.foodieree.model.Video

class VideoAdapter(
    private val context: Context,
    private val videos: ArrayList<Video>,
    private val videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(
         val binding: ItemReelBinding,
         var context : Context,
         private val videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var exoPlayer: ExoPlayer? = null
        private lateinit var mediaSource: MediaSource

        fun setVideoPath(url: String) {
            exoPlayer = ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(context))
                .build().apply {
                    addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            binding.pbLoading.visibility =
                                if (playbackState == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                        }
                    })
                    exoPlayer?.seekTo(0)
                    repeatMode = Player.REPEAT_MODE_ONE
                }

            binding.playerView.player = exoPlayer

            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                if (absoluteAdapterPosition == 0) {
                    playWhenReady = true
                    exoPlayer?.play()
                }
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer!!, absoluteAdapterPosition))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding , context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.binding.reelTitle.text = videos[position].title
        holder.binding.reelLocation.text = videos[position].location
        holder.setVideoPath(videos[position].url)
        updateLikeIcon(holder,videos[position].liked)

        holder.binding.ivLike.setOnClickListener {
            videos[position].liked = !videos[position].liked  // Toggle like state
            updateLikeIcon(holder,videos[position].liked)
        }
    }

    private fun updateLikeIcon(holder: VideoViewHolder,liked: Boolean) {
        val likeIcon = if (liked) R.drawable.bookmark_svgrepo_com else R.drawable.bookmark_svgrepo_com__1_
        holder.binding.ivLike.setImageResource(likeIcon)
    }

    override fun getItemCount() = videos.size

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }
}
