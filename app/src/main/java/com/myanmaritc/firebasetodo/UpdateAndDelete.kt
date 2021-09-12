package com.myanmaritc.firebasetodo

import android.provider.SyncStateContract

interface UpdateAndDelete {

    fun modifyItem(itemObjectId: String, isDone: Boolean)
    fun onItemDelete(itemObjectId: String)
}