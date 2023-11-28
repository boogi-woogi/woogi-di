package com.boogiwoogi.di.injectionexample.fragment.sharedViewModel

import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.commit
import com.boogiwoogi.di.R
import com.boogiwoogi.woogidi.activity.DiActivity
import com.boogiwoogi.woogidi.pure.DefaultModule
import com.boogiwoogi.woogidi.pure.Module

class ExampleActivity : DiActivity() {

    override val module: Module by lazy { DefaultModule() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        setupView()
        setupButtonClickListener()
    }

    private fun setupView() {
        supportFragmentManager.commit {
            add(R.id.example_fcv, FirstFragment())
        }
    }

    private fun setupButtonClickListener() {
        findViewById<Button>(R.id.example_btn_first_fragment).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.example_fcv, FirstFragment())
            }
        }
        findViewById<Button>(R.id.example_btn_second_fragment).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.example_fcv, SecondFragment())
            }
        }
    }
}
