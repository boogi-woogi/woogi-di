package com.boogiwoogi.di.injectionexample.fragment.sharedViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.boogiwoogi.di.R
import com.boogiwoogi.woogidi.fragment.DiFragment
import com.boogiwoogi.woogidi.pure.DefaultModule
import com.boogiwoogi.woogidi.pure.Module
import com.boogiwoogi.woogidi.viewmodel.diActivityViewModels

class SecondFragment : DiFragment() {

    override val module: Module = DefaultModule()

    private lateinit var countText: TextView
    private lateinit var plusButton: Button
    private lateinit var minusButton: Button

    private val exampleViewModel: ExampleViewModel by diActivityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_second, container, false)
            .also {
                countText = it.findViewById(R.id.second_fragment_tv_count)
                plusButton = it.findViewById(R.id.second_fragment_btn_plus)
                minusButton = it.findViewById(R.id.second_fragment_btn_minus)
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupButtonClickListener()
    }

    private fun setupObserver() {
        exampleViewModel.count.observe(viewLifecycleOwner) {
            countText.text = it.toString()
        }
    }

    private fun setupButtonClickListener() {
        plusButton.setOnClickListener {
            exampleViewModel.plus()
        }
        minusButton.setOnClickListener {
            exampleViewModel.minus()
        }
    }
}
