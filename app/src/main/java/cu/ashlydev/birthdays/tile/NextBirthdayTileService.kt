/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.tile

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.TileService
import cu.ashlydev.birthdays.MainActivity

class NextBirthdayTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        qsTile?.apply {
            label = "Cumpleaños"
            state = android.service.quicksettings.Tile.STATE_INACTIVE
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityAndCollapse(
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}