package com.ninjahome.ninja.utils

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object PermissionUtils {

    fun hasStoragePermission(ctx: Context): Boolean {
        return EasyPermissions.hasPermissions(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    fun hasCameraPermission(ctx: Context): Boolean {
        return EasyPermissions.hasPermissions(ctx, Manifest.permission.CAMERA)
    }

}