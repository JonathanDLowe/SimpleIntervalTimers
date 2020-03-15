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
        val ONE_SECOND = 1000L
        val DONE = 0L
    }

    private var timerOneTimeout = 10000L
    private var timerTwoTimeout = 10000L


    private val _timerOneSeconds = MutableLiveData(timerOneTimeout)
    val timerOneSeconds: LiveData<Long>
            get() = _timerOneSeconds
    val timerOneSecondsString = Transformations.map(timerOneSeconds) { time ->
        DateUtils.formatElapsedTime(time)
    }
    private var _timerTwoSeconds = MutableLiveData(timerTwoTimeout)
    val timerTwoSeconds: LiveData<Long>
        get() = _timerTwoSeconds
    val timerTwoSecondsString = Transformations.map(timerTwoSeconds) { time ->
        DateUtils.formatElapsedTime(time)
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
        setTimerOne(10000)
        setTimerTwo(10000)
    }

    lateinit var timerOne: CountDownTimer
    lateinit var timerTwo: CountDownTimer

     fun setTimerOne(millisInFuture: Long) {
         Log.i("TimerViewModl", "setTimerOne()")
        timerOne = object : CountDownTimer(millisInFuture,
            ONE_SECOND
        ) {
            override fun onFinish() {
            _timerOneSeconds.value = DONE
                onTimerOneFinished()
                setTimerTwo(timerTwoTimeout)
                timerTwo.start()
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.i("TimerOne", "onTick")
                _timerOneSeconds.value = (millisUntilFinished / ONE_SECOND)
            }

        }
    }
     fun setTimerTwo(millisInFuture: Long) {
         Log.i("TimerViewModl", "setTimerTwo()")
        timerTwo = object : CountDownTimer(millisInFuture,
            ONE_SECOND
        ) {

            override fun onFinish() {
                _timerTwoSeconds.value = DONE
                onTimerTwoFinished()
                setTimerOne(timerOneTimeout)
                timerOne.start()

            }

            override fun onTick(millisUntilFinished: Long) {
                Log.i("TimerTwo", "onTick")
                _timerTwoSeconds.value = (millisUntilFinished / ONE_SECOND)
            }

        }
    }

    fun onStart() {
        Log.i("TimerViewModl", "onStart()")
        setTimerOne(timerOneTimeout)
        timerOne.start()
        _timerRunning.value = true
    }

    fun onReset() {
        timerOne.cancel()
        timerTwo.cancel()
        _timerRunning.value = false
    }

    private fun onTimerOneFinished() {
        setTimerTwo(timerOneTimeout)
//        timerTwo.start()
    }
    private fun onTimerTwoFinished() {

        setTimerOne(timerOneTimeout)
    }
}