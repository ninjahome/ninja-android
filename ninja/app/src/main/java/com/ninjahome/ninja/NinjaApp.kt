package com.ninjahome.ninja

import com.ninja.android.lib.base.BaseApplication
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.viewmodel.SplashViewModel
import com.ninjahome.ninja.viewmodel.ChatViewModel
import com.ninjahome.ninja.viewmodel.ContactViewModel
import com.ninjahome.ninja.viewmodel.ApplyListViewModel
import com.ninjahome.ninja.viewmodel.MainViewModel
import com.ninjahome.ninja.viewmodel.CreateAccountViewModel
import com.ninjahome.ninja.viewmodel.EditUserInfoViewModel
import com.ninjahome.ninja.viewmodel.MessageViewModel
import com.ninjahome.ninja.viewmodel.MyViewModel
import com.ninjahome.ninja.viewmodel.ScanViewModel
import com.ninjahome.ninja.viewmodel.SearchContactViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class NinjaApp:BaseApplication() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() {

        val appModule = module {

            viewModel { SplashViewModel() }
            viewModel { CreateAccountViewModel() }
            viewModel { EditUserInfoViewModel() }
            viewModel { MainViewModel() }
            viewModel { ContactViewModel() }
            viewModel { MessageViewModel() }
            viewModel { MyViewModel() }
            viewModel { ApplyListViewModel() }
            viewModel { SearchContactViewModel() }
            viewModel { ScanViewModel() }
            viewModel { ChatViewModel() }

            single { CreateAccountModel() }
        }

        startKoin {
            androidLogger()
            androidContext(this@NinjaApp)
            modules(appModule)
        }

    }
}