package com.example.kfsmjs

import com.example.kfsm.Turnstile
import com.example.kfsm.TurnstileEvent
import com.example.kfsm.TurnstileFSM
import com.example.kfsm.TurnstileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            GlobalScope.launch { fsm.coin() }
        })
        passButton.addEventListener("click", {
            GlobalScope.launch { fsm.pass() }
        })
    }

    suspend fun updateViewState() {
        GlobalScope.launch(Dispatchers.Main) {
            val text = when (fsm.currentState()) {
                TurnstileState.LOCKED   -> "Locked"
                TurnstileState.UNLOCKED -> "Unlocked"
            }
            turnstileState.textContent = text
            TurnstileEvent.values().forEach { event ->
                when (event) {
                    TurnstileEvent.PASS -> passButton.disabled = !fsm.allowed(event)
                    TurnstileEvent.COIN -> coinButton.disabled = !fsm.allowed(event)
                }
            }
        }
    }

    suspend fun updateMessage(text: String, error: Boolean) {
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

    override suspend fun lock() {
        require(!locked) { "Expected to be unlocked" }
        _locked = true
        console.log("lock")
        updateViewState()
    }

    override suspend fun unlock() {
        require(locked) { "Expected to be locked" }
        _locked = false
        console.log("unlock")
        updateViewState()
    }

    override suspend fun returnCoin() {
        updateMessage("Return Coin", false)
        console.log("return coin")
        updateViewState()
    }
    override suspend fun timeout() {
        updateMessage("Timeout", true)
        console.log("timeout");
        _locked = true
        updateViewState()
    }
    override suspend fun alarm() {
        updateMessage("Alarm", true)
        console.log("alarm")
        updateViewState()
    }
}

fun main() {
    val handler = TurnstileHandler()
    console.log("Handler init")
    GlobalScope.launch {
        handler.updateViewState()
    }
}
