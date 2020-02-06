package com.example.kfsm.immutable

import com.example.kfsm.immutable.TurnstileEvent.COIN
import com.example.kfsm.immutable.TurnstileEvent.PASS
import com.example.kfsm.immutable.TurnstileState.LOCKED
import com.example.kfsm.immutable.TurnstileState.UNLOCKED
import io.jumpco.open.kfsm.stateMachine

data class TurnstileInfo(
    val locked: Boolean = true,
    val message: String = "",
    val alarm: Boolean = false
) {
    fun update(
        locked: Boolean? = null,
        message: String? = null,
        alarm: Boolean = false
    ): TurnstileInfo {
        return copy(locked ?: this.locked, message ?: "", alarm)
    }
}

enum class TurnstileEvent {
    COIN,
    PASS
}

enum class TurnstileState {
    LOCKED,
    UNLOCKED
}

class TurnstileFSM(turnstile: TurnstileInfo) {
    private val fsm = definition.create(turnstile)

    fun coin(info: TurnstileInfo) = fsm.sendEvent(COIN, info)
    fun pass(info: TurnstileInfo) = fsm.sendEvent(PASS, info)
    fun event(event: String, info: TurnstileInfo) = fsm.sendEvent(TurnstileEvent.valueOf(event.toUpperCase()), info)
    fun allowed(event: TurnstileEvent) = fsm.allowed().contains(event)

    companion object {
        private val definition = stateMachine(
            TurnstileState.values().toSet(),
            TurnstileEvent.values().toSet(),
            TurnstileInfo::class,
            TurnstileInfo::class,
            TurnstileInfo::class
        ) {
            initialState { if (locked) LOCKED else UNLOCKED }
            default {
                action { _, _, info ->
                    require(info != null) { "Info required" }
                    info.update(message = "Alarm")
                }
            }
            whenState(LOCKED) {
                onEvent(COIN to UNLOCKED) { info ->
                    require(info != null) { "Info required" }
                    info.update(locked = false)
                }
            }
            whenState(UNLOCKED) {
                onEvent(PASS to LOCKED) { info ->
                    require(info != null) { "Info required" }
                    info.update(locked = true)
                }
                onEvent(COIN) { info ->
                    require(info != null) { "Info required" }
                    info.update(message = "Return Coin")
                }
            }
        }.build()
    }
}
