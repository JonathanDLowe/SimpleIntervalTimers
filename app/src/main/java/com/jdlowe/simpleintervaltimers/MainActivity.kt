package com.jdlowe.simpleintervaltimers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jdlowe.simpleintervaltimers.databinding.ActivityMainBinding
import com.jdlowe.simpleintervaltimers.timers.TimerViewModel
import com.jdlowe.simpleintervaltimers.timers.TimerViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
        val timerViewModelFactory = TimerViewModelFactory(application)
        val timerViewModel = ViewModelProvider( this, timerViewModelFactory).get(TimerViewModel::class.java)
        timerViewModel.hideSoftInput.observe(this, Observer { hide ->
            Log.i(MainActivity::class.java.simpleName, "hideSoftInput")
            if(hide) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                binding.root.clearFocus()
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                timerViewModel.doneHidingSoftInput()
            }
        })
        binding.timerViewModel = timerViewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }
}
