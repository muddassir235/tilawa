# Tilawa
An Android Library, written in Kotlin, for streaming tilawa of the Qur'an from the web (Currently [mp3quran.net](mp3quran.net) is supported).

## Usage

Define a TilawaProvider
```kotlin
val tilawaProvider = TilawaProvider(context)
```

Get the available Qurra
```kotlin
val qurra = tilawaProvider.qurraInfo

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

Start, Pause and Stop the Tilawa
```kotlin
tilawaProvider.act(start)
tilawaProvider.act(pause)
tilawaProvider.act(stop)
```

Change Qari and Surah
```kotlin
tilawaProvider.act {
  changeSurah(it, SURAH_INFO[1] /* Surah Al-Baqarah */)
}
tilawaProvider.act {
  changeQari(it, tilawaProvider.qurraInfo[1] /* Second available Qari */)
}
```

Go to Next Surah/Previous Surah
```kotlin
tilawaProvider.act(next)
tilawaProvider.act(previous)
```

Recite from the start of the current Surah
```kotlin
tilawaProvider.act(reciteFromStart)
```

Recite from a certain position in time
```kotlin
tilawaProvider.act{
  reciteFrom(it, 60000L /* Recite from 1:00 onwards*/)
}
```

Observe the Tilawa. The observer will be called whenever the Tilawa state change. This can be every second for progress updates, on Qari change, on Surah Change, on audio state change (e.g. start, pause, stop). 
```kotlin
tilawaProvider.act{
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
tilawaProvider.act{ currentTilawa ->
  val newTilawa = //...
  // Your logic
  
  newTilawa ^lambda
}
```

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
