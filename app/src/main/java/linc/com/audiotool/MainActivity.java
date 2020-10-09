package linc.com.audiotool;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import linc.com.library.AudioTool;
import linc.com.library.callback.OnFileComplete;
import linc.com.library.callback.OnListComplete;
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
                    .withAudio(new File("/storage/emulated/0/Music/kygo.mp3"))
                    .getMaxLevelData(1, "/storage/emulated/0/Music/bytes.txt", new OnListComplete() {
                        @Override
                        public void onComplete(List<Float> output) {
                            System.out.println(Arrays.toString(output.toArray()));
                        }
                    })
                    .release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
