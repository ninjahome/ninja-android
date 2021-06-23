package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninjahome.ninja.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainViewModel : BaseViewModel() {
    fun clearCache() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO) {
                FileUtils.deleteDir(File(FileUtils.cacheDir))
            }
        }
    }
}