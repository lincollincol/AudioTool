# AudioTool<img align="right" src="https://github.com/lincollincol/AudioTool/blob/master/img/audio_tool_logo.png" width="200" height="200">
![GitHub](https://img.shields.io/github/license/lincollincol/AudioTool?style=flat-square)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/lincollincol/AudioTool?style=flat-square)
![GitHub All Releases](https://img.shields.io/github/downloads/lincollincol/AudioTool/total?color=%23ffaa&style=flat-square)

![GitHub followers](https://img.shields.io/github/followers/lincollincol?style=social)
![GitHub stars](https://img.shields.io/github/stars/lincollincol/AudioTool?style=social)
![GitHub forks](https://img.shields.io/github/forks/lincollincol/AudioTool?style=social)

AudioTool - an android library that provides useful audio processing functions. This library based on FFMPEG and uses <a href="https://github.com/tanersener/mobile-ffmpeg">mobile-ffmpeg library</a>

### AudioTool provides:

<ul>
  <li> <b>Filters</b>
    <ul>
      <li>filterAudio(. . .)</li>
      <li>removeAudioNoise(. . .) - remove noise from audio</li>
      <li>normalizeAudioVolume(. . .) - normalize audio volume</li>
    </ul>
  </li>
  <li> <b>Equalizer</b>
    <ul>
      <li>changeAudioBass(. . .) - reduce or increase audio bass</li>
      <li>changeAudioVolume(. . .) - reduce or increase audio volume</li>
      <li>changeAudioPitch(. . .) - reduce or increase audio pitch</li>
      <li>changeAudioSpeed(. . .) - reduce or increase audio speed</li>
    </ul>
  </li>
  <li> <b>Effects</b>
     <ul>
      <li>applyShifterEffect(. . .) - apply audio pan shifter effect</li>
      <li>applyReverbEffect(. . .) - apply audio reverb effect</li>
      <li>applyEchoEffect(. . .) - apply audio echo effect</li>
      <li>reverseAudio(. . .) - reverse audio</li>
    </ul>
  </li>
    <li> <b>Modificators</b>
     <ul>
      <li>cutAudio(. . .) - cut audio</li>
      <li>convertVideoToAudio(. . .) - convert video to audio</li>
      <li>joinAudios(. . .) - join few audio files to single</li>
    </ul>
  </li>
  </li>
    <li> <b>Other</b>
     <ul>
      <li>generateWaveform(. . .) - generate image waveform (png)</li>
      <li>getMaxLevelData(. . .) - retrive audio max level data (data can be used to draw waveform)</li>
      <li>getDuration(. . .) - retrive audio duration</li>
      <li>executeFFmpeg(. . .) - execute ffmpeg command</li>
      <li>executeFFprobe(. . .) - execute ffprobe command</li>
    </ul>
  </li>
</ul>

# Example
## Note: every function will modify previous audio.
### Input audio -> cut -> apply effect -> filter -> output audio with all these modifications
``` java
AudioTool.getInstance(this)
  .withAudio(new File("/storage/emulated/0/Music/Linc - AudioTool.mp3"))
  .removeVocal(new OnFileComplete() {
    @Override
    public void onComplete(File output) {
      // Output file - audio without vocal
    }
  })
  .applyEchoEffect(Echo.ECHO_OPEN_AIR, new OnFileComplete() {
    @Override
    public void onComplete(File output) {
      // Output file - audio file with echo effect and without vocal 
    }
  })
                      
  /* calls */
  .release();
```
#### If you want to save current audio file state - use saveCurrentTo(path). Current audio will be saved as separate file and AudioTool continue modify input file from withAudio() parameters.
``` java
AudioTool.getInstance(this)
  .withAudio(new File("/storage/emulated/0/Music/Linc - AudioTool.mp3"))
  .removeVocal(output-> {/* do something with output */})
  .applyEchoEffect(Echo.ECHO_OPEN_AIR, output-> {/* do something with output */})
  .saveCurrentTo("/storage/emulated/0/Music/NewAudio.mp3") // Audio file with echo and without vocal
  .release();
```
#### You can save audio to new file after every function call
``` java
AudioTool.getInstance(this)
  .withAudio(new File("/storage/emulated/0/Music/Linc - AudioTool.mp3"))
  .removeVocal(output-> {/* do something with output */})
  .saveCurrentTo("/storage/emulated/0/Music/Instrumental.mp3") // Audio file without vocal
  .applyEchoEffect(Echo.ECHO_OPEN_AIR, output-> {/* do something with output */})
  .saveCurrentTo("/storage/emulated/0/Music/NewAudio.mp3") // Audio file with echo and without vocal
  .release();
```
#### If don't need result from callbacks, you can pass null as a parameter
``` java
AudioTool.getInstance(this)
  .withAudio(new File("/storage/emulated/0/Music/Linc - AudioTool.mp3"))
  .removeVocal(null) // It's ok. 
  .saveCurrentTo("/storage/emulated/0/Music/Instrumental.mp3") // Save audio without vocal to local file 
  .release();
```
#### Also, don't forget to call release() function when you finish work with AudioTool. The function remove buffer files from storage and clear other resources.
``` java
AudioTool.getInstance(this)
  .withAudio(new File("/storage/emulated/0/Music/Linc - AudioTool.mp3"))
  /* calls */
  .release(); // Always call this function 
```

# Download
## Gradle
``` groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
``` groovy
dependencies {
  implementation 'com.github.lincollincol:AudioTool:1.0'
}
```

## Maven
``` xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
``` xml
<dependency>
  <groupId>com.github.lincollincol</groupId>
  <artifactId>AudioTool</artifactId>
  <version>1.0</version>
</dependency>
```
## Permissions
Add permissions to Manifest.xml file in your app and grant it, before using AudioTool
``` xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## WARNING
### AudioTool process audio in the main thread !  You can run AudioTool functions with RxJava, Kotlin coroutines and Java Threads to process audio in the background therad.
AudioTool don't process audio in the background thread because of :

* You can use your own approach to work in the background thread. It makes AudioTool library more flexible.
* Reduce library size. Third-party library uses a lot of space and AudioTool delegates this task to user.

## Feedback
<a href="https://mail.google.com">linc.apps.sup@gmail.com</a>

# License

```
   Copyright 2020 lincollincol

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
