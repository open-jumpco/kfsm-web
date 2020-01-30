package com.example.kfsmjs

import com.example.kfsm.Turnstile
import com.example.kfsm.TurnstileEvent
import com.example.kfsm.TurnstileFSM
import com.example.kfsm.TurnstileState
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLSpanElement
import kotlin.browser.document
import kotlin.browser.window

class TurnstileHandler : Turnstile {
    private val fsm: TurnstileFSM
    private var _locked: Boolean

    private var turnstileState: HTMLSpanElement
    private var turnstileMessage: HTMLSpanElement
    private var coinButton: HTMLButtonElement
    private var passButton: HTMLButtonElement

    init {
        _locked = true
        fsm = TurnstileFSM(this)
        turnstileState = document.getElementById("turnstileState") as HTMLSpanElement
        turnstileMessage = document.getElementById("turnstileMessage") as HTMLSpanElement
        coinButton = document.getElementById("coinButton") as HTMLButtonElement
        passButton = document.getElementById("passButton") as HTMLButtonElement
        coinButton.addEventListener("click", {
            fsm.coin()
            updateViewState()
        })
        passButton.addEventListener("click", {
            fsm.pass()
            updateViewState()
        })
    }

    fun updateViewState() {
        val text = when (fsm.currentState()) {
            TurnstileState.LOCKED  -> "Locked"
            TurnstileState.UNLOCED -> "Unlocked"
        }
        turnstileState.textContent = text
        TurnstileEvent.values().forEach { event ->
            when (event) {
                TurnstileEvent.PASS -> passButton.disabled = !fsm.allowed(event)
                TurnstileEvent.COIN -> coinButton.disabled = !fsm.allowed(event)
            }
        }
    }

    fun updateMessage(text: String, error: Boolean) {
        val color = if (error) {
            "red"
        } else {
            "blue"
        }
        turnstileMessage.style.color = color
        turnstileMessage.style.fontWeight = if (error) "Bold" else "Normal"
        turnstileMessage.textContent = text

        window.setTimeout({ turnstileMessage.textContent = "" }, if (error) 5000 else 2000)
    }

    override val locked: Boolean
        get() = _locked

    override fun lock() {
        require(!locked) { "Expected to be unlocked" }
        _locked = true
        console.log("lock")
    }

    override fun unlock() {
        require(locked) { "Expected to be locked" }
        _locked = false
        console.log("unlock")
    }

    override fun returnCoin() {
        updateMessage("Return Coin", false)
        console.log("return coin")
    }

    override fun alarm() {
        updateMessage("Alarm", true)
        console.log("alarm")
    }
}

fun main() {
    val handler = TurnstileHandler()
    console.log("Handler init")
    handler.updateViewState()
}
