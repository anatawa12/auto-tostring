package com.anatawa12.autoToString.compiler

import org.jetbrains.kotlin.descriptors.CallableDescriptor

class ToStringMethodData(
    val properties: List<String>
) {
    companion object : CallableDescriptor.UserDataKey<ToStringMethodData>
}
