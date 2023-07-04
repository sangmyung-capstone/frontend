package com.bapool.bapool.retrofit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PartyInfoViewModel : ViewModel() {
    private var sharedObjectInfo = MutableLiveData<FirebasePartyInfo>()


    fun setObjectInfo(newObjectInfo: FirebasePartyInfo) {
        sharedObjectInfo.value = newObjectInfo
    }

    fun getObjectInfo(): FirebasePartyInfo? {
        return sharedObjectInfo.value
    }

}
