# AudioTool<img align="right" src="https://github.com/lincollincol/AudioTool/blob/master/img/audio_tool_logo.png" width="200" height="200">
![GitHub](https://img.shields.io/github/license/lincollincol/AudioTool?style=flat-square)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/lincollincol/AudioTool?style=flat-square)
![GitHub All Releases](https://img.shields.io/github/downloads/lincollincol/AudioTool/total?color=%23ffaa&style=flat-square)

![GitHub followers](https://img.shields.io/github/followers/lincollincol?style=social)
![GitHub stars](https://img.shields.io/github/stars/lincollincol/AudioTool?style=social)
![GitHub forks](https://img.shields.io/github/forks/lincollincol/AudioTool?style=social)

AudioTool - an android library that provides useful audio processing functions. This library based on FFMPEG and uses <a href="https://github.com/tanersener/mobile-ffmpeg">mobile-ffmpeg library</a>

### AudioTool provide such functions:
* Filters (filter, bass, noise, vocal, noise)
* Effects (shifter, reverb, echo, reverse)
* Eq (bass, volume, normalize, pitch, speed)
* Modificators (cut, video 2 audio)
* other (waveform, duration, max levels, join, exec ffmpeg / ffprobe)


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