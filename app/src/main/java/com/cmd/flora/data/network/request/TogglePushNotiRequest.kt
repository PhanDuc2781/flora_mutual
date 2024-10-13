package com.cmd.flora.data.network.request

import com.cmd.flora.data.network.base.Decodable

data class TogglePushNotiResponse(
    val enable: Boolean?,
): Decodable