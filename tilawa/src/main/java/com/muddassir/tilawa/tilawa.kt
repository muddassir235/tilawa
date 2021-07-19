package com.muddassir.tilawa

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.muddassir.faudio.ActualAudioState
import com.muddassir.faudio.Audio
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class Tilawa(context: Context, owner: LifecycleOwner? = null) {
    private val scope = (context as? AppCompatActivity)?.lifecycleScope
        ?: owner?.lifecycleScope ?: GlobalScope

    private val audio = Audio(context, owner)
    private var qariInfo: QariInfo? = null

    private val _state = MutableLiveData<ActualTilawaState>()
    val state: LiveData<ActualTilawaState> = _state

    private val observer: (ActualAudioState) -> Unit = {
        if(qariInfo != null) {
            _state.value = audioStateToTilawaState(qariInfo!!, it)
        }
    }

    init {
        if(owner != null) {
            audio.state.observe(owner, observer)
        } else {
            audio.state.observeForever(observer)
        }
    }

    suspend fun setState(newState: ExpectedTilawaState): Boolean {
        qariInfo = newState.qariInfo
        return audio.setState(tilawaStateToAudioState(newState))
    }

    suspend fun changeState(action: (ActualTilawaState) -> ExpectedTilawaState): Boolean {
        val newState = this._state.value?.change(action) ?: return false
        return this.setState(newState)
    }

    fun setStateAsync(newState: ExpectedTilawaState, callback: ((Boolean) -> Unit)? = null) {
        flow {
            emit(setState(newState))
        }.onEach {
            callback?.invoke(it)
        }.launchIn(scope)
    }

    fun changeStateAsync(action: (ActualTilawaState) -> ExpectedTilawaState,
                         callback: ((Boolean)->Unit)? = null) {
        flow {
            emit(changeState(action))
        }.onEach {
            callback?.invoke(it)
        }.launchIn(scope)
    }

    fun release() {
        audio.release()
    }
}