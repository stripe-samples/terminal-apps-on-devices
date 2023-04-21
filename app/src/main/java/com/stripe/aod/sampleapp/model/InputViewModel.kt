package com.stripe.aod.sampleapp.model

import androidx.lifecycle.ViewModel
import java.util.regex.Pattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InputViewModel : ViewModel() {
    enum class ACTION {
        Add, Delete, Clear
    }

    private val pattern = Pattern.compile("0*")

    private val _amount: MutableStateFlow<String> = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _showModifierKeys: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showModifierKeys: StateFlow<Boolean> = _showModifierKeys.asStateFlow()

    private fun addAmountCharacter(w: Char?): String {
        if (w == null) return _amount.value
        if (_amount.value.length >= 8) {
            return _amount.value
        }
        if (w == '0' && pattern.matcher(_amount.value).matches()) {
            return _amount.value
        }
        _amount.value += w
        return _amount.value
    }

    private fun deleteAmountCharacter(): String {
        if (_amount.value.isEmpty()) {
            _amount.value = ""
            return _amount.value
        }
        _amount.value = _amount.value.substring(0, _amount.value.length - 1)
        return _amount.value
    }

    private fun clearAmount(): String {
        _amount.value = ""
        return _amount.value
    }

    fun displayAmount(amt: Char? = null, action: ACTION = ACTION.Add) {
        val amountValue = when (action) {
            ACTION.Add -> {
                addAmountCharacter(amt)
            }
            ACTION.Delete -> {
                deleteAmountCharacter()
            }
            ACTION.Clear -> {
                clearAmount()
            }
        }
        _amount.value = amountValue
        _showModifierKeys.value = _amount.value.isNotBlank()
    }
}
