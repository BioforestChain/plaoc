package org.bfchain.libappmgr.di

import org.bfchain.libappmgr.ui.dcim.DCIMViewModel
import org.bfchain.libappmgr.ui.download.DownLoadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.bfchain.libappmgr.ui.main.MainViewModel

val viewModelModule = module {
  viewModel { MainViewModel() }
  viewModel { DownLoadViewModel() }
  viewModel { DCIMViewModel() }
}
