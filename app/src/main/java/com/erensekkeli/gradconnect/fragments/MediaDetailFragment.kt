package com.erensekkeli.gradconnect.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.erensekkeli.gradconnect.databinding.FragmentMediaDetailBinding
import com.erensekkeli.gradconnect.models.Media


class MediaDetailFragment : Fragment() {

    private lateinit var binding: FragmentMediaDetailBinding
    private lateinit var media: Media

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMediaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        @Suppress("DEPRECATION")
        media = arguments?.getSerializable("media") as Media

        binding.mediaTitle.text = media.title ?: ""
        binding.mediaDescription.text = media.description ?: ""

        if(media.mediaType == "image") {
           //create image view
            val imageView = ImageView(context)
            imageView.id = View.generateViewId()
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            Glide.with(this).load(media.mediaUrl).into(imageView)
            binding.mediaCard.addView(imageView)
        } else {
            //create video view
            val videoPath = media.mediaUrl
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            var width = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
            var height = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
            retriever.release()
            if(width < 500) {
                width *= 3
            }
            if(width < 1000) {
                width *= 2
            }
            if(height < 500) {
                height *= 3
            }
            if(height < 1000) {
                height *= 2
            }
            binding.mediaVideoView.layoutParams.width = width
            binding.mediaVideoView.layoutParams.height = height
            binding.mediaVideoView.setVideoPath(media.mediaUrl)
            //get video size
            binding.mediaVideoView.visibility = View.VISIBLE
            binding.mediaPlayButton.visibility = View.VISIBLE
            binding.mediaVideoView.setOnClickListener {
                if(binding.mediaVideoView.isPlaying) {
                    binding.mediaVideoView.pause()
                    binding.mediaPlayButton.visibility = View.VISIBLE
                } else {
                    binding.mediaVideoView.start()
                    binding.mediaPlayButton.visibility = View.INVISIBLE
                }
            }
            binding.mediaPlayButton.bringToFront()

        }


        binding.backBtn.setOnClickListener {
            getBack()
        }
    }

    private fun getBack() {
        val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.popBackStack()
    }

}