package com.example.cameraapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isGone
import androidx.lifecycle.LifecycleOwner
import com.example.cameraapp.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var text_left: Animation
    lateinit var text_right: Animation

    private val REQUEST_CODE: Int = 100

    lateinit var cameraProvider: ListenableFuture<ProcessCameraProvider>
    lateinit var imageCapture: ImageCapture
    lateinit var videoCapture: VideoCapture
    lateinit var cameraSelector: CameraSelector


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE;

        checkPermission()
        setIvSave()
        text_left = AnimationUtils.loadAnimation(this, R.anim.text_anime_left)
        text_right = AnimationUtils.loadAnimation(this, R.anim.text_anime_right)
        cameraSelector=CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()


        cameraProvider = ProcessCameraProvider.getInstance(this)
        cameraProvider.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProvider.get()
                startCameraX(cameraProvider)
            } catch (_: Exception) {

            }
        }, getExecutor())

        binding.tvPhoto.setOnClickListener {
            setBackground(binding.tvPhoto)

            binding.ivMode.setImageResource(R.drawable.iv_image)
        }
        binding.tvVideo.setOnClickListener {
            setBackground(binding.tvVideo)
            binding.tvPhoto.isClickable = true

            binding.ivMode.setImageResource(R.drawable.iv_record)
        }

        binding.ivMode.setOnClickListener {
            setIvMode()

        }

        binding.ivSwitch.setOnClickListener{
       if(cameraSelector.lensFacing==CameraSelector.LENS_FACING_BACK){
                if(cameraProvider.get().hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)){
                    cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                }
                else{
                    Toast.makeText(this@MainActivity,"Front Camera Not working !",Toast.LENGTH_SHORT).show()
                }

            } else{
           cameraSelector =  CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            }
            cameraProvider.addListener({
                try {
                    val cameraProvider: ProcessCameraProvider = cameraProvider.get()
                    startCameraX(cameraProvider)
                } catch (_: Exception) {

                }
            }, getExecutor())


        }


        binding.ivSave.setOnClickListener{
            startActivity(Intent(this@MainActivity,SavedActivity::class.java))
        }


    }

    private fun setIvSave() {

                val imageFiles =
                    File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CameraApp/Image")

                var imageFile: File? = null
                if (imageFiles.exists()) {
                    for (file in imageFiles.listFiles()?.reversed()!!) {
                        imageFile = File(file.absolutePath)
                        break
                    }

                }
                val videoFiles =
                    File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CameraApp/Video")
                var videoFile: File? = null
                if (videoFiles.exists()) {
                    for (file in videoFiles.listFiles()?.reversed()!!) {
                        videoFile = File(file.absolutePath)
                        break
                    }
                }


                    if (imageFile != null && videoFile != null) {
                        val imageTime = imageFile.lastModified()
                        val videoTime = videoFile.lastModified()
                        if (imageTime >= videoTime) {
                            binding.ivSave.setImageURI(Uri.parse(imageFile.absolutePath))
                        } else {
                            binding.ivSave.setImageBitmap(
                                ThumbnailUtils.createVideoThumbnail(
                                    videoFile.absolutePath.toString(),
                                    MediaStore.Images.Thumbnails.MINI_KIND
                                )
                            )
                        }
                    } else {
                        if (imageFile != null) {
                            binding.ivSave.setImageURI(Uri.parse(imageFile.absolutePath))
                        } else if(videoFile!=null) {
                            binding.ivSave.setImageBitmap(
                                ThumbnailUtils.createVideoThumbnail(
                                    videoFile?.absolutePath.toString(),
                                    MediaStore.Images.Thumbnails.MINI_KIND
                                )
                            )
                        }
                        else{
                            binding.ivSave.setImageResource(R.drawable.iv_save)
                        }
                    }


    }

    private fun getExecutor(): Executor {
        return ContextCompat.getMainExecutor(this)
    }

    @SuppressLint("RestrictedApi")
    private fun startCameraX(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()


        val preview=Preview.Builder().build()
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)



        Log.e("Preview","${binding.previewView.surfaceProvider}")



         imageCapture=ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
        videoCapture=VideoCapture.Builder().setVideoFrameRate(30).build()
        cameraProvider.bindToLifecycle(this@MainActivity as LifecycleOwner,cameraSelector,preview,imageCapture,videoCapture)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        var permissionsBelow11 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

        )
        val permissionsAbove10= arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
        {
            for (permission in permissionsAbove10) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    requestPermission(permissionsAbove10)
                }
            }
        }
        else{

            for (permission in permissionsBelow11) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    requestPermission(permissionsBelow11)
                }
            }

        }

    }

    private fun requestPermission(permissions: Array<String>) {

        ActivityCompat.requestPermissions(this@MainActivity, permissions, REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Please Provide permission for using app! ${permissions[0].toString()}",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


    @SuppressLint("RestrictedApi")
    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun setIvMode() {
        if (binding.ivMode.drawable.toBitmap().sameAs(
                ContextCompat.getDrawable(this@MainActivity, R.drawable.iv_image)?.toBitmap()
            )
        ) {
            startImageCapturing()

        } else {
            if (binding.ivMode.drawable.toBitmap().sameAs(
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.iv_record)?.toBitmap()
                )
            ) {
                binding.ivMode.setImageResource(R.drawable.iv_stop_record)
                binding.tvPhoto.isGone=true
                binding.tvVideo.isGone=true
                binding.timer.isGone=false
                binding.timer.base = SystemClock.elapsedRealtime()
                binding.timer.start()
                startVideoCapturing()
            } else {
                binding.tvPhoto.isGone=false
                binding.tvVideo.isGone=false
                binding.timer.isGone=true
                binding.timer.base = SystemClock.elapsedRealtime()
                binding.timer.stop()
                binding.ivMode.setImageResource(R.drawable.iv_record)
               videoCapture.stopRecording()


            }
        }
    }

    @SuppressLint("RestrictedApi")
    @RequiresPermission("android.permission.RECORD_AUDIO")
    private fun startVideoCapturing() {
        val dir1= File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CameraApp")

        if(!dir1.exists()){
            dir1.mkdir()
        }

        val dir2=File("$dir1/Video")
        if(!dir2.exists()){
            dir2.mkdir()
        }

        val fileName="${System.currentTimeMillis()}.mp4"
        val picPath="$dir2/$fileName"
        val savePath=File(picPath)

        videoCapture.startRecording(VideoCapture.OutputFileOptions.Builder(savePath).build(),getExecutor(),object :VideoCapture.OnVideoSavedCallback{
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                Toast.makeText(this@MainActivity, "Video Saved this location -> ${savePath.absolutePath}", Toast.LENGTH_SHORT)
                    .show()
                setIvSave()
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                Toast.makeText(this@MainActivity, "Video Not Saved !", Toast.LENGTH_SHORT)
                    .show()
            }


        })


    }

    private fun startImageCapturing() {
        val dir1= File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/CameraApp")

        if(!dir1.exists()){
            dir1.mkdir()
        }

        val dir2=File("$dir1/Image")
        if(!dir2.exists()){
            dir2.mkdir()
        }
        val fileName="${System.currentTimeMillis()}.jpg"
        val picPath="$dir2/$fileName"
        val savePath=File(picPath)

        imageCapture.takePicture(ImageCapture.OutputFileOptions.Builder(savePath).build(),getExecutor(),object :ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(this@MainActivity, "Pic Saved this location -> ${savePath.absolutePath}", Toast.LENGTH_SHORT)
                    .show()
                setIvSave()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@MainActivity, "Pic Not Saved !", Toast.LENGTH_SHORT)
                    .show()
            }

        })


    }

    fun setBackground(view: TextView) {
        when (view.id) {
            R.id.tvPhoto -> {
                view.setTextColor(getColor(R.color.yellow))
                view.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.text_back)
                binding.tvVideo.setTextColor(Color.WHITE)
                binding.tvVideo.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.text_back_trans
                )
            }

            R.id.tvVideo -> {
                view.setTextColor(getColor(R.color.yellow))
                view.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.text_back)
                binding.tvPhoto.setTextColor(Color.WHITE)
                binding.tvPhoto.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.text_back_trans
                )
            }
        }

    }
}