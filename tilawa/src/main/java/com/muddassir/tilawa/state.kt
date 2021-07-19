package com.muddassir.tilawa

data class ActualTilawaState(
    val qariInfo             : QariInfo,
    val recitationIndex      : Int,
    val surahIndex           : Int,
    val paused               : Boolean,
    val progress             : Long,
    val bufferedPosition     : Long,
    val currentIndexDuration : Long,
    val stopped              : Boolean,
    val error                : String?
) {
    val surahInfo: SurahInfo get() = qariInfo.recitations[recitationIndex]
        .availableSuvar[surahIndex]

    fun change(action: (ActualTilawaState) -> ExpectedTilawaState): ExpectedTilawaState {
        return action(this)
    }
}

data class ExpectedTilawaState(
    val qariInfo             : QariInfo,
    val recitationIndex      : Int,
    val surahIndex           : Int,
    val paused               : Boolean,
    val progress             : Long,
    val stopped              : Boolean
) {
    val surahInfo: SurahInfo get() = qariInfo.recitations[recitationIndex]
        .availableSuvar[surahIndex]

    companion object {
        fun defaultWithQariInfo(qariInfo: QariInfo): ExpectedTilawaState {
            return ExpectedTilawaState(
                qariInfo,
                0,
                0,
                true,
                0,
                true
            )
        }
    }
}