package linc.com.audiotool;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import linc.com.library.AudioTool;
import linc.com.library.Duration;
import linc.com.library.Echo;

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
                    .withAudio(new File("/storage/emulated/0/Music/kygo.mp3"))
                    .cutAudio(1, 60, null)
                    .saveCurrentTo("/storage/emulated/0/Music/cut_25_s.mp3")
    //                .changeAudioPitch(44100, 3, 1.24f, null)
    //                .saveCurrentTo("/storage/emulated/0/Music/pitch_3.mp3")
    //                .applyEchoEffect(Echo.ECHO_FEW_MOUNTAINS, null)
    //                .saveCurrentTo("/storage/emulated/0/Music/echo_few.mp3")
    //                .applyReverbEffect(0.5f, 0.7f, null)
    //                .saveCurrentTo("/storage/emulated/0/Music/reverb.mp3")
    //                .applyShifterEffect(1, 0.5f, null)
    //                .saveCurrentTo("/storage/emulated/0/Music/shifter.mp3")
    //                .applyVibratoEffect(1000, 0.5f, null)
    //                .saveCurrentTo("/storage/emulated/0/Music/vibrato.mp3")
    //                .generateWaveform(200, 400, "#fafafa", "/storage/emulated/0/Music/waveka.png", null)
                    .release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
