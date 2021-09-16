package com.clxns.app.ui.home.account


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyProfileVMFactory(val application: Application,val myProfileRepo: MyProfileRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  MyProfileViewModel(application,myProfileRepo) as T
    }
}