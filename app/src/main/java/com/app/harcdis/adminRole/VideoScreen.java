package com.app.harcdis.adminRole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.harcdis.R;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class VideoScreen extends AppCompatActivity {
    StyledPlayerView style_player_view;
    ExoPlayer exoPlayer;
    private String videoUrl = "";
    private PlayerView playerView;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);
        videoView = findViewById(R.id.video_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoUrl = bundle.getString("video_url");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                byte[] videoBytes = Base64.getDecoder().decode(videoUrl);
                File tempVideoFile = saveVideoToTempFile(videoBytes);

                if (tempVideoFile != null) {
                    // Set up a MediaController for playback controls
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoView);

                    // Set the MediaController to the VideoView
                    videoView.setMediaController(mediaController);
                    // Set the video source to the temporary file and start playback
                    videoView.setVideoPath(tempVideoFile.getPath());
                    videoView.start();
                }else{
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

        }



    }

    private File saveVideoToTempFile(byte[] videoBytes) {
        try {
            File tempDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (tempDir != null) {
                File tempVideoFile = new File(tempDir, "temp_video.mp4");
                FileOutputStream fos = new FileOutputStream(tempVideoFile);
                fos.write(videoBytes);
                fos.close();
                return tempVideoFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}