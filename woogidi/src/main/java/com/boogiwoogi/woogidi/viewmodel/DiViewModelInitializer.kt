package com.boogiwoogi.woogidi.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.boogiwoogi.woogidi.activity.DiActivity
import com.boogiwoogi.woogidi.application.DiApplication
import com.boogiwoogi.woogidi.fragment.DiFragment
import com.boogiwoogi.woogidi.pure.InstanceContainer
import com.boogiwoogi.woogidi.pure.Module

@MainThread
inline fun <reified VM : ViewModel> DiActivity.diViewModels(): Lazy<VM> = ViewModelLazy(
    VM::class,
    { viewModelStore },
    { diViewModelFactory<VM>(instanceContainer, module) },
)

inline fun <reified VM : ViewModel> diViewModelFactory(
    container: InstanceContainer,
    module: Module
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        DiApplication.run {
            injector.inject<VM>(
                module = module,
                container = container
            )
        }
    }
}

/**
 * viewModel will not be sharing each fragments
 */
@MainThread
inline fun <reified VM : ViewModel> DiFragment.diViewModels(): Lazy<VM> = ViewModelLazy(
    VM::class,
    { viewModelStore },
    { diViewModelFactory<VM>(instanceContainer, module) },
)

/**
 * viewModel will be sharing each fragments contained on same activity
 */
@MainThread
inline fun <reified VM : ViewModel> DiFragment.diActivityViewModels(): Lazy<VM> = ViewModelLazy(
    VM::class, { requireActivity().viewModelStore },
    { diViewModelFactory<VM>(instanceContainer, module) }
)
