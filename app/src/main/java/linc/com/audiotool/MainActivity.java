package linc.com.audiotool;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import linc.com.library.AudioTool;
import linc.com.library.callback.OnFFmpegComplete;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);

        try {
            AudioTool.getInstance(this)
                    .withAudio("/storage/emulated/0/Music/level.mp3")
//                    .changeAudioPitch(44100, -0.86883157f, -0.8699582800000001f, null)
//                    .changeAudioPitch(44100, -2.86883157f, null)
//                    .changeAudioPitch(44100, -2.86883157f, Pitch.DOWN, null)
                    .saveCurrentTo("/storage/emulated/0/Music/level_pitch.mp3")
                    .executeFFmpeg("", new OnFFmpegComplete() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    })
                    .release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
