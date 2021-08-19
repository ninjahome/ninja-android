package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:2021/8/18
 *Description:
 */
enum class Verifylicense(val value: Long) {
    DecodeLicenseErr(0), ConnectionErr(1), ContractErr(2), CallContractErr(3), ValidTrue(4), ValidFalse(5)
}