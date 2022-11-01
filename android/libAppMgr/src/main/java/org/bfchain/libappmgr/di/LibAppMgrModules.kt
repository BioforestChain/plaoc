package org.bfchain.libappmgr.di

import org.bfchain.libappmgr.ui.dcim.DCIMViewModel
import org.bfchain.libappmgr.ui.download.DownLoadViewModel
import org.bfchain.libappmgr.ui.main.MainRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.bfchain.libappmgr.ui.main.MainViewModel
import org.bfchain.libappmgr.ui.main.MainViewModel2

val viewModelModule = module {
  viewModel { MainViewModel() }
  viewModel { DownLoadViewModel() }
  viewModel { DCIMViewModel() }
  viewModel { MainViewModel2(get()) }
}

val repositoryModule = module {
  single { MainRepository(get()) }
}
