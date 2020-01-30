package com.example.kfsm

import io.jumpco.open.kfsm.stateMachine

interface Turnstile {
    val locked: Boolean

    fun lock()
    fun unlock()
    fun returnCoin()
    fun alarm()
}

enum class TurnstileEvent {
    COIN,
    PASS
}

enum class TurnstileState {
    LOCKED,
    UNLOCED
}

class TurnstileFSM(val turnstile: Turnstile) {
    companion object {
        private val definition = stateMachine(
            TurnstileState.values().toSet(),
            TurnstileEvent.values().toSet(), Turnstile::class
        ) {
            initialState { if (locked) TurnstileState.LOCKED else TurnstileState.UNLOCED }
            default {
                action { _, _, _ ->
                    alarm()
                }
            }
            whenState(TurnstileState.LOCKED) {
                onEvent(TurnstileEvent.COIN to TurnstileState.UNLOCED) {
                    unlock()
                }
            }
            whenState(TurnstileState.UNLOCED) {
                onEvent(TurnstileEvent.PASS to TurnstileState.LOCKED) {
                    lock()
                }
                onEvent(TurnstileEvent.COIN) {
                    returnCoin()
                }
            }
        }.build()
    }

    private val fsm = definition.create(turnstile)
    fun currentState() = fsm.currentState
    fun coin() = fsm.sendEvent(TurnstileEvent.COIN)
    fun pass() = fsm.sendEvent(TurnstileEvent.PASS)
    fun allowed(event: TurnstileEvent) = fsm.allowed().contains(event)
}
