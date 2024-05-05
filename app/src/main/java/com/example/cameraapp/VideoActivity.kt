package com.example.cameraapp

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.cameraapp.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)


        val uri = Uri.parse(intent.getStringExtra("video"))

        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        binding.videoView.start()

        if(savedInstanceState!=null){
            val currentPos=savedInstanceState.getInt("position")
            binding.videoView.seekTo(currentPos)
        }

        if(binding.videoView.isPlaying){
            savedInstanceState?.putInt("position",binding.videoView.currentPosition)
        }

    }

    override fun onPause() {
        super.onPause()
        binding.videoView.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
        binding.videoView.stopPlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
    }
}