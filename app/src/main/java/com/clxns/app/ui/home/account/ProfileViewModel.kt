package com.clxns.app.ui.home.account

import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.clxns.app.R
import com.clxns.app.data.repository.ProfileRepository
import com.clxns.app.utils.NetworkHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {




}