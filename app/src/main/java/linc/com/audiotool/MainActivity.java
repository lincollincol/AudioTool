package linc.com.audiotool;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import linc.com.library.AudioTool;
import linc.com.library.types.Echo;

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
                    .withAudio(new File("/storage/emulated/0/Music/cut_5_s.mp3"))
//                    .withAudio(new File("/storage/emulated/0/Music/kygo.mp3"))
//                    .cutAudio(1, 5, null)
//                    .saveCurrentTo("/storage/emulated/0/Music/cut_5_s.mp3")

//                    .changeAudioPitch(44100, 3, 1.24f, null)
//                    .saveCurrentTo("/storage/emulated/0/Music/pitch_3.mp3")

//                    .applyEchoEffect(Echo.ECHO_FEW_MOUNTAINS, null)
//                    .saveCurrentTo("/storage/emulated/0/Music/echo_few.mp3")

                    .applyReverbEffect(0.5f, 0.7f, null)
                    .saveCurrentTo("/storage/emulated/0/Music/reverb.mp3")



                    .release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
