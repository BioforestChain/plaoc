package org.bfchain.libappmgr.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.bfchain.libappmgr.ui.main.MainViewModel

val viewModelModule = module {
  viewModel { MainViewModel() }
}
