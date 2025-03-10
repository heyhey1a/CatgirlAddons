package catgirlroutes.commands.impl

import catgirlroutes.CatgirlRoutes.Companion.display
import catgirlroutes.commands.commodore
import catgirlroutes.module.impl.render.ClickGui
import catgirlroutes.ui.misc.searchoverlay.AhBzSearch
import catgirlroutes.ui.misc.searchoverlay.OverlayType
import catgirlroutes.utils.ChatUtils.modMessage
import catgirlroutes.utils.LocationManager.inSkyblock

val catgirlAddonsCommands = commodore("catgirladdons", "cataddons", "cga") {
    runs {
        ClickGui.onEnable()
    }

    literal("help").runs { // todo: add description
        modMessage("""
            List of commands:
              §7/cga
              §7/pearlclip §5[§ddepth§5]
              §7/lavaclip §5[§ddepth§5]
              §7/blockclip §5[§ddistance§5]
              §7/node
              §7/p3
              §7/dev
              §7/cgaaura
              §7/cga ah
              §7/cga bz
        """.trimIndent())
    }

    literal("ah").runs {
        if (inSkyblock) {
            display = AhBzSearch(OverlayType.AUCTION)
        } else modMessage("You're not in skyblock")
    }

    literal("bz").runs {
        if (inSkyblock) {
            display = AhBzSearch(OverlayType.BAZAAR)
        } else modMessage("You're not in skyblock")
    }
}