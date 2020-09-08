# Tilawa تلاوه القرآن الكريم (Android, Kotlin)
[![Release](https://jitpack.io/v/muddassir235/tilawa.svg?style=flat-square)](https://jitpack.io/#muddassir235/tilawa/)

An Android Library, written in Kotlin, for streaming tilawa of the Qur'an from the web (Currently [mp3quran.net](mp3quran.net) is supported).

Currently the following Qurra are supported,
        
* Sheikh Abdur Rahman as Sudais الشيخ عبد الرحمن السديس
* Sheikh Saud bin Ibrahim ash Shuraim الشيخ سعود بن ابراهيم الشريم
* Sheikh Salah bin Muhammad al Budair الشيخ صلاح بن محمد البدير
* Sheikh Ali bin Abdur Rahman al Hudhaify الشيخ علي بن عبد الرحمن الحذيفي
* Sheikh Shiekh Maher al Mu'aiqly الشيخ ماهر بن حمد المعيقلي
* Sheikh Abdullah 'awad Al Juhainy الشيخ عبد الله عواد الجهني
* Sheikh Muhammad al Luhaidan الشيخ محمد اللحيدان
* Sheikh Mishary bin Rashid al Afasy الشيخ مشاري بن راشد العفاسي
* Sheikh Ali Jaber الشيخ علي جابر
* Sheikh Bandar Baleela الشيخ بندر بليله
* Sheikh Abdul Basit Abdus Samad الشيخ عبـد الباسـط عبـد ٱلصـمـد
* Sheikh Raad Muhammad al Kurdi الشيخ رعد محمد الكردی
* Sheikh Ahmed bin Ali al 'Ajmi الشيخ أحمد بن علي العجمي

## Add Dependencies
Add the following in your project level build.gradle
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
and the following in your app level build.gradle
```groovy
dependencies {
    implementation 'com.github.muddassir235:tilawa:1.1'
}
```

## Usage

Define a TilawaProvider. Tilawa state persistance is handled out of the box (So when the app is reopened and tilawa is started it will continue when it was left off when the app was closed)

```kotlin
val tilawaProducer = TilawaProducer(context)
```

Get the available Qurra
```kotlin
val qurra = tilawaProducer.qurraInfo

val firstQari = qurra[0]

firstQari.arabicName
firstQari.englishName
firstQari.availableSuvar // Availabe Suvar for the qari
firstQari.audioServerUrl // Url of the server the Tilawa mp3 are hosted on.

val firstSurah = firstQari.availableSuvar[0]

firstSurah.number        // Surah number [0,113]
firstSurah.arabicName 
firstSurah.englishName
firstSurah.makkiOrMadani // Value is either "makki" or "madani"
```

Start, pause and stop the Tilawa
```kotlin
tilawaProducer.act(start)
tilawaProducer.act(pause)
tilawaProducer.act(stop)
```

Change Qari and Surah
```kotlin
tilawaProducer.act {
  changeSurah(it, SUVAR_INFO[1] /* Surah Al-Baqarah */)
}
tilawaProducer.act {
  changeQari(it, tilawaProvider.qurraInfo[1] /* Second available Qari */)
}
```

Go to the next/previous Surah
```kotlin
tilawaProducer.act(next)
tilawaProducer.act(previous)
```

Recite from the start of the current Surah
```kotlin
tilawaProducer.act(reciteFromStart)
```

Recite from a certain position in time
```kotlin
tilawaProducer.act{
  reciteFrom(it, 60000L /* Recite from 1:00 onwards */)
}
```

Observe the Tilawa. The observer will be called whenever the Tilawa state change. This can be every second for progress updates, on Qari change, on Surah Change, on audio state change (e.g. start, pause, stop). 
```kotlin
tilawaProducer.observation() // Current state of the tilawa
```
Or you can add a listener to receive all state changes.
```kotlin
tilawaProducer.act{
  addObserver(it) { observation ->
    // observation.qariInfo
    // observation.surahInfo
    // observation.audioStateInfo.error
    // observation.audioStateInfo.stopped
    // observation.audioStateInfo.paused
    // observation.audioStateInfo.index
    // observation.audioStateInfo.position
    // observation.audioStateInfo.bufferedPosition
    // observation.audioStateInfo.duration
  }
}
```

Perform a custom action on the tilawa
```kotlin
tilawaProducer.act{ currentTilawa ->
  val newTilawa = //...
  // Your logic
  
  newTilawa // ^lambda
}
```
## Uses
* https://github.com/muddassir235/kmacros
* https://github.com/muddassir235/eprefs
* https://github.com/muddassir235/faudio

## License
```
MIT License

Copyright (c) 2020 Muddassir Ahmed Khan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
