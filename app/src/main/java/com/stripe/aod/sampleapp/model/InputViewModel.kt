package com.stripe.aod.sampleapp.model

import androidx.lifecycle.ViewModel
import java.util.regex.Pattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InputViewModel : ViewModel() {
    enum class Action {
        Add, Delete, Clear
    }

    private val pattern = Pattern.compile("0*")

    private val _amount: MutableStateFlow<String> = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _showModifierKeys: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showModifierKeys: StateFlow<Boolean> = _showModifierKeys.asStateFlow()

    private fun addAmountCharacter(w: Char?): String {
        _amount.update { currentAmount ->
            if (w == null || currentAmount.length >= 8 ||
                (w == '0' && pattern.matcher(currentAmount).matches())
            ) {
                currentAmount
            } else {
                currentAmount + w
            }
        }
        return _amount.value
    }

    private fun deleteAmountCharacter(): String {
        _amount.update { currentAmount ->
            if (currentAmount.isEmpty()) {
                currentAmount
            } else {
                currentAmount.substring(0, currentAmount.length - 1)
            }
        }
        return _amount.value
    }

    private fun clearAmount(): String {
        _amount.update { "" }
        return _amount.value
    }

    fun displayAmount(amt: Char? = null, action: Action = Action.Add) {
        val amountValue = when (action) {
            Action.Add -> {
                addAmountCharacter(amt)
            }
            Action.Delete -> {
                deleteAmountCharacter()
            }
            Action.Clear -> {
                clearAmount()
            }
        }
        _amount.update { amountValue }
        _showModifierKeys.update { _amount.value.isNotBlank() }
    }
}
