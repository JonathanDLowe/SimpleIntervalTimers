package com.jdlowe.simpleintervaltimers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jdlowe.simpleintervaltimers.databinding.ActivityMainBinding
import com.jdlowe.simpleintervaltimers.timers.TimerViewModel
import com.jdlowe.simpleintervaltimers.timers.TimerViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
        val timerViewModelFactory = TimerViewModelFactory(application)
        binding.timerViewModel = ViewModelProvider( this, timerViewModelFactory).get(TimerViewModel::class.java)
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }
}
