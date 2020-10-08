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
                    .joinAudios(new String[]{
                            "/storage/emulated/0/Music/reverse.mp3",
                            "/storage/emulated/0/Music/bass.mp3",
                            "/storage/emulated/0/Music/shifter.mp3"
                    }, "/storage/emulated/0/Music/join.mp3", null)

                    .release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
