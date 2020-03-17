package com.jdlowe.simpleintervaltimers.timers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.nfc.Tag
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.AndroidException
import android.util.Log
import android.view.View
import android.view.animation.Transformation
import androidx.core.content.edit
import androidx.lifecycle.*

class TimerViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        val TAG = "TimerViewModel"
        val SHARED_PREFERENCES = "TIMER_VIEW_MODEL_PREFS"
        val KEY_TIMER_ONE_TIMEOUT = "KEY_TIMER_ONE_TIMEOUT"
        val KEY_TIMER_TWO_TIMEOUT = "KEY_TIMER_TWO_TIMEOUT"
        val ONE_SECOND = 1000L
        val DONE = 0L
        val DEFAULT_TIMER_VALUE = 10000L
        val TIMER_LOWER_BOUND = 5L
        val TIMER_UPPER_BOUND = 60L
    }

    private var timerOneTimeout: Long
    private var timerTwoTimeout: Long
    lateinit var timerOne: CountDownTimer
    lateinit var timerTwo: CountDownTimer
    var sharedPreferences: SharedPreferences


    init {
        sharedPreferences = application.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        timerOneTimeout = sharedPreferences.getLong(KEY_TIMER_ONE_TIMEOUT, DEFAULT_TIMER_VALUE)
        timerTwoTimeout = sharedPreferences.getLong(KEY_TIMER_TWO_TIMEOUT, DEFAULT_TIMER_VALUE)

    }

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
        setTimerTwo(timerTwoTimeout)
        timerTwo.start()
    }
    private fun onTimerTwoFinished() {
        _timerTwoMilliseconds.value = timerTwoTimeout
        setTimerOne(timerOneTimeout)
        timerOne.start()
    }

    private val _timerOneEditingVisible = MutableLiveData(false)
    val timerOneEditingVisibility = Transformations.map(_timerOneEditingVisible) { isVisible ->
        if(isVisible) View.VISIBLE
        else View.INVISIBLE

    }
    val timerOneVisibility = Transformations.map(_timerOneEditingVisible) {isVisible ->
        if(isVisible) View.INVISIBLE
        else View.VISIBLE

    }
    private val _timerTwoEditingVisible = MutableLiveData(false)
    val timerTwoEditingVisibility = Transformations.map(_timerTwoEditingVisible) { isVisible ->
        if(isVisible) View.VISIBLE
        else View.INVISIBLE
    }

    val timerTwoVisibility = Transformations.map(_timerTwoEditingVisible) {isVisible ->
        if(isVisible) View.INVISIBLE
        else View.VISIBLE
    }

    fun onTimerOneClicked() {
        _timerOneEditingVisible.value = true

    }

    fun onTimerTwoClicked() {
        _timerTwoEditingVisible.value = true

    }

    fun onTimerOneEditingDone(input: String) {
        Log.i(TAG, input)
        var parsedInput = input.toLongOrNull() ?: TIMER_LOWER_BOUND
        parsedInput = ensureNumberInRange(TIMER_LOWER_BOUND, TIMER_UPPER_BOUND, parsedInput)
        parsedInput *= ONE_SECOND
        setTimerOne(parsedInput)
        _timerOneEditingVisible.value = false
        sharedPreferences.edit {
            putLong(KEY_TIMER_ONE_TIMEOUT, parsedInput)
            commit()
        }
        timerOneTimeout = parsedInput

    }

    fun onTimerTwoEditingDone(input: String) {
        Log.i(TAG, input)
        var parsedInput = input.toLongOrNull() ?: TIMER_LOWER_BOUND
        parsedInput = ensureNumberInRange(TIMER_LOWER_BOUND, TIMER_UPPER_BOUND, parsedInput)
        parsedInput *= ONE_SECOND
        setTimerTwo(parsedInput)
        _timerTwoEditingVisible.value = false
        sharedPreferences.edit {
            putLong(KEY_TIMER_TWO_TIMEOUT, parsedInput)
            commit()
        }
        timerTwoTimeout = parsedInput

    }

    private fun ensureNumberInRange(lowerBound: Long, upperBound: Long, value: Long) : Long {
        var num = value
        num = Math.max(num, lowerBound)
        num = Math.min(num, upperBound)
        return num
    }

//    var timerOneInput = (timerOneTimeout / ONE_SECOND).toString()
//    var timerTwoInput = (timerTwoTimeout / ONE_SECOND).toString()
    var timerOneInput = timerOneSecondsString.value
    var timerTwoInput = timerTwoSecondsString.value


}

class TimerViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if ( modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}