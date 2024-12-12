package catgirlroutes.ui.notification

import catgirlroutes.CatgirlRoutes.Companion.RESOURCE_DOMAIN
import catgirlroutes.CatgirlRoutes.Companion.mc
//import catgirlroutes.ui.clickgui.util.ColorUtil
import catgirlroutes.utils.Notifications
import catgirlroutes.utils.render.HUDRenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

class Notification(
    val title: String,
    val description: String,
    private val duration: Double,
    val type: NotificationType,
    val icon: String? = null
) {
    val startTime: Long = System.currentTimeMillis()
    val endTime: Long = startTime + duration.toLong()

    fun draw(x: Double, y: Double, width: Double, height: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

//        HUDRenderUtils.renderRect(x, y, width, height, Color(ColorUtil.bgColor)) // bg
        HUDRenderUtils.renderRectBorder(x, y, width, height, 1.0, this.type.getColour()) // border

        //easing stuff
        val elapsed = System.currentTimeMillis() - startTime
        val rawProgress = (elapsed / duration).coerceIn(0.0, 1.0)
        val easedProgress = Notifications.Easing.easeOutQuad(rawProgress) //change function from Easing for different animations
        HUDRenderUtils.renderRect(x, y, width * easedProgress, height, this.type.getColour().darker())

        val iconOffset = icon?.let {
            val texture = ResourceLocation(RESOURCE_DOMAIN, it)
            GlStateManager.enableBlend()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(texture)
            Gui.drawModalRectWithCustomSizedTexture(
                x.toInt() + 3, (y + (height - 40) / 2).toInt(),
                0f, 0f, 40, 40, 40f, 40f
            )
            GlStateManager.disableBlend()
            43 // offset
        } ?: 0

        val lines = wrapText(this.description, width - 12 - iconOffset)
        val lineSpacing = mc.fontRendererObj.FONT_HEIGHT + 2
        val totalTextHeight = (lines.size + 1) * lineSpacing
        val textX = x.toFloat() + iconOffset + 3
        val textY = y + (height - totalTextHeight) / 2

        // title
        mc.fontRendererObj.drawStringWithShadow(this.title, textX, textY.toFloat(), Color.WHITE.rgb)

        // description
        lines.forEachIndexed { index, line ->
            mc.fontRendererObj.drawStringWithShadow(line, textX + 3, textY.toFloat() + (lineSpacing * (index + 1)), Color.WHITE.rgb)
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun wrapText(text: String, maxWidth: Double): List<String> { // shit to split long description
        val lines = mutableListOf<String>()
        var currentLine = ""

        text.split(" ").forEach { word ->
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (mc.fontRendererObj.getStringWidth(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }
}