package ru.rtuitlab.itlab.presentation.ui.extensions

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState


private val stateTranslationTable = HashMap<Pair<Boolean, Boolean>, TransitionState>().apply {
	this[false to false] = TransitionState.Appearing
	this[false to true] = TransitionState.Disappearing
	this[true to false] = TransitionState.Invisible
	this[true to true] = TransitionState.Visible
}

private val dynamicStateTranslationTable = HashMap<Pair<TransitionState, TransitionState>, TransitionState>().apply {
	this[TransitionState.Disappearing to TransitionState.Invisible] = TransitionState.ArrivedToInvisible
	this[TransitionState.Invisible to TransitionState.Appearing] = TransitionState.InitiatedToInvisible
}

@ExperimentalTransitionApi
fun MutableTransitionState<Boolean>.transitionState(
	previousState: TransitionState,
	onUpdate: (TransitionState) -> Unit
): TransitionState {
	val previousTransitionState = previousState
	val incomingTransitionState = this.incomingTransitionState(onUpdate)
	return dynamicStateTranslationTable[previousTransitionState to incomingTransitionState] ?: incomingTransitionState
}

@ExperimentalTransitionApi
private fun MutableTransitionState<Boolean>.incomingTransitionState(onUpdate: (TransitionState) -> Unit): TransitionState =
	stateTranslationTable[this.isIdle to this.currentState]!!.also { onUpdate(it) }

enum class TransitionState {
	Visible,
	Appearing,
	Invisible,
	Disappearing,

	ArrivedToInvisible,
	InitiatedToInvisible
}