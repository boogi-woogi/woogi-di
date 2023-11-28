package com.boogiwoogi.di.injectionexample.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExampleViewModel(
    private val exampleRepository: ExampleRepository
) : ViewModel() {

    private val _count: MutableLiveData<Int> = MutableLiveData(0)
    val count: LiveData<Int>
        get() = _count

    init {
        fetchData()
    }

    fun fetchData() {
        Log.d("injectionTest", "fetchData: ${exampleRepository.fetchData()}")
    }

    fun plus() {
        _count.value = _count
            .value
            ?.plus(1)
    }

    fun minus() {
        _count.value = _count
            .value
            ?.minus(1)
    }
}
