package linc.com.audiotool;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import linc.com.library.AudioTool;
import linc.com.library.Duration;

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

        AudioTool.getInstance(this)
                .withAudio(new File("/storage/emulated/0/Music/kygo.mp3"))
                .cutAudio(0, 0, new AudioTool.OnFileComplete() {
                    @Override
                    public void onComplete(File output) {

                    }
                })
                .getDuration(Duration.SECONDS, new AudioTool.OnNumberComplete() {
                    @Override
                    public void onComplete(Long output) {
                        System.out.println("Duration from audio (sec) = " + output);
                    }
                })
                .getDuration(Duration.MILLIS, new AudioTool.OnNumberComplete() {
                    @Override
                    public void onComplete(Long output) {
                        System.out.println("Duration from audio (millis) = " + output);
                    }
                })
                .getDuration(Duration.MINUTES, new AudioTool.OnNumberComplete() {
                    @Override
                    public void onComplete(Long output) {
                        System.out.println("Duration from audio (min) = " + output);
                    }
                })
                .release();
    }
}
