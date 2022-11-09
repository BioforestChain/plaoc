package info.bagen.libappmgr.di

import info.bagen.libappmgr.ui.dcim.DCIMViewModel
import info.bagen.libappmgr.ui.download.DownLoadViewModel
import info.bagen.libappmgr.ui.main.MainRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import info.bagen.libappmgr.ui.main.MainViewModel
import info.bagen.libappmgr.ui.main.MainViewModel2

val viewModelModule = module {
  viewModel { MainViewModel() }
  viewModel { DownLoadViewModel() }
  viewModel { DCIMViewModel() }
  viewModel { MainViewModel2(get()) }
}

val repositoryModule = module {
  single { MainRepository(get()) }
}
