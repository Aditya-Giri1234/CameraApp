package com.example.cameraapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cameraapp.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivImage.setImageURI(Uri.parse(intent.getStringExtra("image")))
    }
}