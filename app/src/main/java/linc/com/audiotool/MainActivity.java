package linc.com.audiotool;

import android.Manifest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

import linc.com.library.AudioTool;
import linc.com.library.callback.OnFileComplete;
import linc.com.library.callback.OnListComplete;
import linc.com.library.callback.OnNumberComplete;
import linc.com.library.types.Duration;
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
                    .withAudio("/storage/emulated/0/Music/Linc - AudioTool.mp3")
                    .applyEchoEffect(Echo.ECHO_FEW_MOUNTAINS, null)
                    .saveCurrentTo("/storage/emulated/0/Music/Linc - AudioTool_With_Echo.mp3")
                    .release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
