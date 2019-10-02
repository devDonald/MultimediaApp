package com.donald.multimediaapp

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import java.io.IOException
import android.net.Uri
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    //TODO 2: Create global variable to be used in initializing views
    private lateinit var take_picture: Button
    private lateinit var record_video: Button
    private lateinit var start_record: Button
    private lateinit var stop_record: Button
    private lateinit var pause_record: Button
    private lateinit var picture_display: ImageView
    private lateinit var video_display: VideoView

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    //two constants to specify our actions, either we are picking images from gallery or camera
    private val GALLERY = 1
    private val CAMERA = 2
    //to specify our video capture action
    private val VIDEO_CAPTURE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO 3: Initialize your views

        take_picture = findViewById(R.id.bt_take_picture)
        record_video = findViewById(R.id.bt_record_video)
        picture_display = findViewById(R.id.im_display_image)
        video_display = findViewById(R.id.vv_display_video)
        start_record = findViewById(R.id.bt_start_recording)
        stop_record = findViewById(R.id.bt_stop_recording)
        pause_record = findViewById(R.id.bt_pause_recording)



        //TODO 3: Set onclick listenner on the take picture button
        take_picture.setOnClickListener(View.OnClickListener {

            //TODO 4: we use alert dialog here to choose either to take picture from gallery or camera

            //create an object of the alert dialog
            val pictureDialog = AlertDialog.Builder(this)

            // we set out title
            pictureDialog.setTitle("Select Action")

            //we specify the options on this line
            val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
            //we set our actions here. if user select any option what should it do
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    //action 1 chooses image from the gallery
                    0 -> choosePhotoFromGallary()//this function that performs the action is below
                    //action 2 takes a photo from the camera
                    1 -> takePhotoFromCamera()//this function that perform this action is below
                }
            }
            //always put this line for the dialog to show
            pictureDialog.show()
        })


        record_video.setOnClickListener(View.OnClickListener {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
            } else {
                recordVideo()
            }


        })



    }


    //TODO 5: create the choosePhoto function
    fun choosePhotoFromGallary() {

        //create an object of an Intent that picks files for you and spcify that it should pick images
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    //TODO 6: Create the takePhotoFromCamera function
    private fun takePhotoFromCamera() {
        //allows you to use your phone's camera to snap pitures
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    //TODO 7: creates the function that captures the result of every of your action
    //After selecting an image from gallery or capturing photo from camera, an onActivityResult() method is executed.
    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // checks if we picked image from Gallery
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                //gets the image we picked
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    //displays the image for us on our image view
                    picture_display!!.setImageBitmap(bitmap)

                }
                //catches erros if there is any
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        //checks if we snapped picture with camera
        else if (requestCode == CAMERA)
        {
            //gets the image we took
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            //displays the image on our image view
            picture_display!!.setImageBitmap(thumbnail)
            Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }


        //TODO 9: Handles the recorded video
        //gets the video recorded
        //when you install your app go to the application details in your settings and grant permissions
        // if not your app will crash if you click on record video

        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri= data?.data
            Toast.makeText(this, "Video has been saved to:\n" + videoUri, Toast.LENGTH_LONG).show();

            //sets the Video recorded on video Viewgit
            video_display.setVideoURI(videoUri)
            video_display.setMediaController(android.widget.MediaController(this))
            video_display.requestFocus()
            video_display.start()
        }
    }


    //Handles the video recording
    fun recordVideo(){
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takeVideoIntent, VIDEO_CAPTURE)
                }
            }
        } else {
            Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show()
        }
    }

}
