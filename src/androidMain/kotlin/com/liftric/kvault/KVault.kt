package com.liftric.kvault

import android.content.Context
import com.liftric.kvault.impl.KVaultImpl

fun KVault(
    context: Context,
    fileName: String? = null
): KVault = KVaultImpl(context, fileName)