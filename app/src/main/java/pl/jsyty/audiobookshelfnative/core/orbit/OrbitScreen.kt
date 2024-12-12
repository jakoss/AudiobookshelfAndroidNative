package pl.jsyty.audiobookshelfnative.core.orbit

import cafe.adriel.voyager.core.model.*
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container

/**
 * All screen models in the application should be based on this class.
 *
 * It's just a convenience helper over standard orbit-mvi [ContainerHost] <-> [ScreenModel] integration.
 *
 * @param STATE State of the ScreenModel
 * @param SIDE_EFFECT Side effect type that ScreenModel can emit (pass [Unit] if you won't emit side effect). If you have multiple side effect use sealed class to represent those
 * @constructor Setup Orbit-MVI [container]
 *
 * @param initialState Initial state of ScreenModel. In most cases it will just call parameterless [STATE] constructor to stick with default properties values.
 */
abstract class OrbitScreenModel<STATE : Any, SIDE_EFFECT : Any>(initialState: STATE) : ScreenModel,
    ContainerHost<STATE, SIDE_EFFECT> {
    override val container = screenModelScope.container<STATE, SIDE_EFFECT>(initialState)
}
