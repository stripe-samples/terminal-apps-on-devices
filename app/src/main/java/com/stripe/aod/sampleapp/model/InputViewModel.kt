package com.stripe.aod.sampleapp.model

import androidx.lifecycle.ViewModel
import com.stripe.aod.sampleapp.utils.formatAmount
import java.util.regex.Pattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InputViewModel : ViewModel() {
    enum class ACTION {
        ADD, DELETE, CLEAR
    }

    private val pattern = Pattern.compile("0*")

    private val _amt: MutableStateFlow<String> = MutableStateFlow("")
    val amt: StateFlow<String> = _amt.asStateFlow()

    private val _showModifierKeys: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showModifierKeys: StateFlow<Boolean> = _showModifierKeys.asStateFlow()

    private var amount = ""

    private fun addAmountCharacter(w: Char?): String {
        if (w == null) return amount
        if (amount.length >= 8) {
            return amount
        }
        if (w == '0' && pattern.matcher(amount).matches()) {
            return amount
        }
        amount += w
        return amount
    }

    private fun deleteAmountCharacter(): String {
        if (amount.isEmpty()) {
            amount = ""
            return amount
        }
        amount = amount.substring(0, amount.length - 1)
        return amount
    }

    private fun clearAmount(): String {
        amount = ""
        return amount
    }

    fun displayAmount(amt: Char? = null, action: ACTION = ACTION.ADD) {
        val amountValue = when (action) {
            ACTION.ADD -> {
                addAmountCharacter(amt)
            }
            ACTION.DELETE -> {
                deleteAmountCharacter()
            }
            ACTION.CLEAR -> {
                clearAmount()
            }
        }

        val value = buildString {
            var remainLength = amount.length
            while (remainLength < 3) {
                append('0')
                ++remainLength
            }

            append(amountValue)
            insert(remainLength - 2, '.')
        }

        _amt.value = "${'$'}${formatAmount(value)}"
        _showModifierKeys.value = !("0.00" == value || _amt.value.isEmpty())
    }

    override fun onCleared() {
        super.onCleared()
        amount = ""
    }
}
