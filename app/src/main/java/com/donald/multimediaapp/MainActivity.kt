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
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var take_picture: Button
    private lateinit var record_video: Button
    private lateinit var start_record: Button
    private lateinit var stop_record: Button
    private lateinit var pause_record: Button
    private lateinit var picture_display: ImageView
    private lateinit var video_display: VideoView
    private lateinit var play_record:Button

    //for audio voice recording
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    //two constants to specify our actions, either we are picking images from gallery or camera
    private val GALLERY = 10
    private val CAMERA = 21
    //to specify our video capture action
    private val VIDEO_CAPTURE = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        take_picture = findViewById(R.id.bt_take_picture)
        record_video = findViewById(R.id.bt_record_video)
        picture_display = findViewById(R.id.im_display_image)
        video_display = findViewById(R.id.vv_display_video)
        start_record = findViewById(R.id.bt_start_recording)
        stop_record = findViewById(R.id.bt_stop_recording)
        pause_record = findViewById(R.id.bt_pause_recording)

        //TODO 2: Link the play Button
        play_record =findViewById(R.id.bt_play_recording)


        //TODO 3: Add lines 70 to 83

        //gets the instance of the MediaRecorder
        mediaRecorder = MediaRecorder()
        //Define the part you want to store the recording
        output = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"

        //set how you want to get the recording. we are using MIC
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        //set your output format
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        //convert the recording to mp3
        mediaRecorder?.setOutputFile(output)

        take_picture.setOnClickListener(View.OnClickListener {


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

        //TODO 4: set the Start record button listenner and then check if permissions are accepted

        start_record.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
            } else {
                //call the start recording function
                startRecording()
            }
        }


        //TODO 5: set the stop recording button
        stop_record.setOnClickListener{
            //call the stop recording function
            stopRecording()
        }

        //TODO 6: set the pause recording Button
        pause_record.setOnClickListener {
            //call the pause recording function
            pauseRecording()
        }

        //TODO 7: set the play recording Button
        play_record.setOnClickListener(View.OnClickListener {

            //call the play recording function
            playRecording()
        })

    }


    fun choosePhotoFromGallary() {

        //create an object of an Intent that picks files for you and spcify that it should pick images
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        //allows you to use your phone's camera to snap pitures
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(intent, CAMERA)
    }

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
            //gets the image we snapped with the camera
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            //displays the image on our image view
            picture_display!!.setImageBitmap(thumbnail)
            Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }


        //gets the video recorded
        //when you install your app go to the application details in your settings and grant permissions
        // if not your app will crash if you click on record video

        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri= data?.data
            Toast.makeText(this, "Video has been saved to:\n" + videoUri, Toast.LENGTH_LONG).show();

            //sets the Video recorded on video View
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

    //start recording function
    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //pause recording function
    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        try {
            if(state) {
                if(!recordingStopped){
                    Toast.makeText(this,"Stopped!", Toast.LENGTH_SHORT).show()
                    mediaRecorder?.pause()
                    recordingStopped = true
                    pause_record.text = "Resume"
                }else{
                    resumeRecording()
                }
            }

        } catch (e:java.lang.Exception){

        }

    }

    //resume recording function
    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        try {
            Toast.makeText(this,"Resume!", Toast.LENGTH_SHORT).show()
            mediaRecorder?.resume()
            pause_record.text = "Pause"
            recordingStopped = false

        } catch (e:java.lang.Exception){

        }

    }

    //stop recording function
    private fun stopRecording(){
        try {
            if(state){
                mediaRecorder?.stop()
                mediaRecorder?.release()
                state = false
                Toast.makeText(this, "Recording Stopped!", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
            }
        } catch (e:java.lang.Exception){

        }

    }

    //play recording function
    private fun playRecording(){

        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(output)
            mediaPlayer.prepare()
            mediaPlayer.start()
            Toast.makeText(this, "Playing Audio", Toast.LENGTH_LONG).show()

        } catch (e:Exception) {
            // make something
        }

    }

}
