package com.clxns.app.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val mobileNumberError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)