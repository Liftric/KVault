package com.liftric.kvault

import com.liftric.kvault.impl.KVaultImpl

fun KVault(
    serviceName: String? = null,
    accessGroup: String? = null,
    accessibility: KVaultImpl.Accessible = KVaultImpl.Accessible.WhenUnlocked
): KVault = KVaultImpl(serviceName, accessGroup, accessibility)
