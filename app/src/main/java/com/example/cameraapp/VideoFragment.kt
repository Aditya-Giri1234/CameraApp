package com.example.cameraapp

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cameraapp.adapter.MediaAdapter
import com.example.cameraapp.databinding.FragmentVideoBinding
import java.io.File

class VideoFragment : Fragment() {

    lateinit var binding:FragmentVideoBinding
    var list=ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentVideoBinding.inflate(layoutInflater)
        val files= File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CameraApp/Video")

        if(files.exists()){
            for(file in files.listFiles()!!){
                list.add(file.absolutePath)
            }
        }
        val adapter= MediaAdapter(requireContext(),list,false)
        binding.rvVideo.layoutManager= LinearLayoutManager(requireContext())
        binding.rvVideo.adapter=adapter

        return binding.root
    }

}