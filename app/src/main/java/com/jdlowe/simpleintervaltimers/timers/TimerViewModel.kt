package com.jdlowe.simpleintervaltimers.timers

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    companion object {
        val TAG = "TimerViewModel"
        val ONE_SECOND = 1000L
        val DONE = 0L
    }

    private var timerOneTimeout = 10000L //TODO
    private var timerTwoTimeout = 10000L
    lateinit var timerOne: CountDownTimer
    lateinit var timerTwo: CountDownTimer

    private val _timerOneMilliseconds = MutableLiveData(timerOneTimeout)
    val timerOneMilliseconds: LiveData<Long>
        get() = _timerOneMilliseconds
    val timerOneSecondsString = Transformations.map(timerOneMilliseconds) { time ->
        DateUtils.formatElapsedTime(time / ONE_SECOND)
    }
    private var _timerTwoMilliseconds = MutableLiveData(timerTwoTimeout)
    val timerTwoMilliseconds: LiveData<Long>
        get() = _timerTwoMilliseconds
    val timerTwoSecondsString = Transformations.map(timerTwoMilliseconds) { time ->
        DateUtils.formatElapsedTime(time / ONE_SECOND)
    }
    private val _timerRunning = MutableLiveData<Boolean>(false)
    val timersRunning: LiveData<Boolean>
        get() = _timerRunning

    val startButtonVisible = Transformations.map(timersRunning) {running ->
        if (running != true) View.VISIBLE
        else View.INVISIBLE

    }
    val resetButtonVisible = Transformations.map(timersRunning) {running ->
        if (running == true) View.VISIBLE
        else View.INVISIBLE
    }

    init {
        setTimerOne(timerOneTimeout)
        setTimerTwo(timerTwoTimeout)
    }

    fun setTimerOne(millisInFuture: Long) {
        Log.i("TimerViewModl", "setTimerOne()")
        timerOneTimeout = millisInFuture;
        _timerOneMilliseconds.value = millisInFuture
        timerOne = object : CountDownTimer(millisInFuture,
            ONE_SECOND
        ) {
            override fun onFinish() {
                _timerOneMilliseconds.value = DONE
                onTimerOneFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.i("TimerOne", "onTick")
                _timerOneMilliseconds.value = millisUntilFinished
            }
        }
    }
    fun setTimerTwo(millisInFuture: Long) {
        Log.i("TimerViewModl", "setTimerTwo()")
        timerTwoTimeout = millisInFuture;
        _timerTwoMilliseconds.value = millisInFuture
        timerTwo = object : CountDownTimer(millisInFuture,
            ONE_SECOND
        ) {

            override fun onFinish() {
                _timerTwoMilliseconds.value = DONE
                onTimerTwoFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.i("TimerTwo", "onTick")
                _timerTwoMilliseconds.value = millisUntilFinished
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared")
    }

    fun onStart() {
        Log.i("TimerViewModel", "onStart()")
        setTimerOne(timerOneTimeout)
        timerOne.start()
        _timerRunning.value = true
    }

    fun onReset() {
        timerOne.cancel()
        timerTwo.cancel()
        _timerRunning.value = false
        setTimerOne(timerOneTimeout)
        setTimerTwo(timerTwoTimeout)
    }

    private fun onTimerOneFinished() {
        _timerOneMilliseconds.value = timerOneTimeout
        setTimerTwo(timerOneTimeout)
        timerTwo.start()
    }
    private fun onTimerTwoFinished() {
        _timerTwoMilliseconds.value = timerTwoTimeout
        setTimerOne(timerOneTimeout)
        timerOne.start()
    }
}