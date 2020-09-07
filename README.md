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

