package com.muddassir.tilawa

import java.io.Serializable

data class SurahInfo(val number        : Int    = -1,
                     val arabicName    : String = "",
                     val englishName   : String = "",
                     val makkiOrMadani : String = ""): Serializable

data class QariInfo(val number         : Int     = -1,
                    val arabicName     : String  = "",
                    val englishName    : String  = "",
                    val recitations    : Array<RecitationInfo>,
                    var isFavorite     : Boolean = false): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QariInfo

        if (number != other.number) return false
        if (arabicName != other.arabicName) return false
        if (englishName != other.englishName) return false
        if (!recitations.contentEquals(other.recitations)) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number
        result = 31 * result + arabicName.hashCode()
        result = 31 * result + englishName.hashCode()
        result = 31 * result + recitations.contentHashCode()
        result = 31 * result + isFavorite.hashCode()
        return result
    }
}

data class RecitationInfo(
    val serverUrl: String,
    val description: String? = null,
    val availableSuvar : Array<SurahInfo> = SUVAR_INFO
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecitationInfo

        if (serverUrl != other.serverUrl) return false
        if (description != other.description) return false
        if (!availableSuvar.contentEquals(other.availableSuvar)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverUrl.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + availableSuvar.contentHashCode()
        return result
    }
}