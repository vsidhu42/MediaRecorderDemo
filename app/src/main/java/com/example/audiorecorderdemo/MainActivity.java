package com.example.audiorecorderdemo;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    // I used this tutorial
    // https://www.tutorialspoint.com/android/android_audio_capture.htm

    // VARINDER SIDHU
    // RECORDING AND PLAYING BACK AUDIO

    // What is absolutely mandatory:
    // 1) The import statements
    //          - MediaPlayer (Playback)
    //          - MediaRecorder (Recording)
    // 2) The 3 uses-permission statements from the manifest file
    //          - WRITE_EXTERNAL_STORAGE
    //          - RECORD_AUDIO
    //          - STORAGE
    // 3) Initialize a MediaRecorder object
    // 4) Set the MediaRecorder Settings
    //          - setAudioSource(Which audio input)
    //          - setOutputFormat(file format)
    //          - setAudioEncoder(bitrate?)
    //          - setOutputFile(output file path and filename(*** With correct file extension ***) )
    // 5) Call the MediaRecorder Methods
    //          - prepare()
    //          - start()
    //          - stop()

    // the buttons on the activity
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;

    // will save the path to the recording
    String AudioSavePathInDevice = null;

    // the recorder object
    MediaRecorder mediaRecorder ;

    // permission code
    public static final int RequestPermissionCode = 1;

    // media player object
    MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button initialization
        buttonStart = (Button) findViewById(R.id.btnRecord);
        buttonStop = (Button) findViewById(R.id.btnStop);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.btnPlay);
        buttonStopPlayingRecording = (Button)findViewById(R.id.btnStopPlaying);

        // disable buttons
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        // records the audio
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The user must first allow permission to access the data on the device
                if(checkPermission()) {

                    // Sets the save/load path of the audio file
                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().
                                    getAbsolutePath() + "/AudioRecording.3gp";

                    // sets the recorder settings
                    MediaRecorderReady();

                    // Starts the recording, catch exception if an error occurs
                    try {
                        // ensures the initialization is complete
                        mediaRecorder.prepare();
                        // starts recording
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // once the recorder is recording, enable these buttons again
                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {

                    // ask for permissions if they haven't been granted already
                    requestPermission();
                }

            }
        });

        // stops recording
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // stops recording
                mediaRecorder.stop();

                // enables/disables buttons
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(MainActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        // plays the last recorded audio clip
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                // enables/disables buttons
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                // create a mediaplayer for playback
                mediaPlayer = new MediaPlayer();

                // set the data source for playback as the saved recording
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // I set an OnCompletion listener which runs once
                // the audio file has finished playing
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {

                        // enables/disables buttons
                        buttonStop.setEnabled(false);
                        buttonStart.setEnabled(true);
                        buttonStopPlayingRecording.setEnabled(false);
                        buttonPlayLastRecordAudio.setEnabled(true);

                        Toast.makeText(MainActivity.this, "Playback Complete",
                                Toast.LENGTH_LONG).show();
                    }
                });

                // play the recorded file
                mediaPlayer.start();

                Toast.makeText(MainActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

        // stops the mediaplayer
        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // stops the media player
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

    }

    // Initializes the MediaRecorder
    // Sets the MediaRecorder settings
    public void MediaRecorderReady(){
        // initialize the MediaRecorder
        mediaRecorder=new MediaRecorder();

        // set the MediaRecorder Settings
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    // ask for storage permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    // checks what permission was given
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            // RequestPermissionCode is a final value equal to 1
            // this will always run... i think
            case RequestPermissionCode:
                // if permission is granted, display in a toast that permission was granted
                // do nothing if permission was denied
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    // This simply checks to make sure the app has the permissions required
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}