package com.example.cameraapp

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cameraapp.adapter.MediaAdapter
import com.example.cameraapp.databinding.FragmentImageFragementBinding
import java.io.File


class ImageFragment : Fragment() {


    lateinit var binding: FragmentImageFragementBinding
        var list=ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentImageFragementBinding.inflate(layoutInflater)

        val files=File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CameraApp/Image")

        if(files.exists()){
            for(file in files.listFiles()!!){
                list.add(file.absolutePath)
            }
        }
        val adapter=MediaAdapter(requireContext(),list,true)
        binding.rvImage.layoutManager=LinearLayoutManager(requireContext())
        binding.rvImage.adapter=adapter

        return binding.root

    }
}