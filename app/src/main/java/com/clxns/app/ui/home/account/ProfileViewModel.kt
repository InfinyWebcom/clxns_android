package com.clxns.app.ui.home.account

import androidx.lifecycle.ViewModel
import com.clxns.app.data.repository.ProfileRepository
import com.clxns.app.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {




}