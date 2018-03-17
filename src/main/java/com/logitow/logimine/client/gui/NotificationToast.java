package com.logitow.logimine.client.gui;

import com.logitow.bridge.communication.Device;
import com.logitow.logimine.LogiMine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Toast ui with logitow notifications.
 */
@SideOnly(Side.CLIENT)
public class NotificationToast implements IToast {
    /**
     * Location of the toast textures.
     */
    public static final ResourceLocation TEXTURE_TOASTS = new ResourceLocation(LogiMine.modId, "gui/notification-toast.png");

    /**
     * Type of the toast.
     */
    private final NotificationToast.Type type;
    /**
     * Title text of the toast.
     */
    private String title;
    /**
     * Subtitle text of the toast.
     */
    private String subtitle;
    private long firstDrawTime;
    private boolean newDisplay;


    //Translated strings.
    public static final ITextComponent TEXT_CONNECTED_TITLE = new TextComponentTranslation("logitow.toast.connected.title");
    public static final String TEXT_CONNECTED_SUBTITLE_KEY = "logitow.toast.connected.subtitle";
    public static final ITextComponent TEXT_DISCONNECTED_TITLE = new TextComponentTranslation("logitow.toast.disconnected.title");
    public static final String TEXT_DISCONNECTED_SUBTITLE_KEY = "logitow.toast.disconnected.subtitle";

    /**
     * New toast notification.
     * @param toastType
     * @param title
     * @param subtitle
     */
    public NotificationToast(NotificationToast.Type toastType, String title, @Nullable String subtitle) {
        this.type = toastType;
        this.title = title;
        this.subtitle = subtitle;
    }

    /**
     * Draws the notification graphic.
     * @param toastGui
     * @param delta
     * @return
     */
    public IToast.Visibility draw(GuiToast toastGui, long delta) {
        //Prolonging the show time.
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }

        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        GlStateManager.color(1.0F, 1.0F, 1.0F); //white

        toastGui.drawTexturedModalRect(0, 0, 0, type.textureOffset, 160, 32);

        if (this.subtitle == null) {
            toastGui.getMinecraft().fontRenderer.drawStringWithShadow(this.title, 28, 12, type.titleColor);
        } else {
            toastGui.getMinecraft().fontRenderer.drawStringWithShadow(this.title, type.textXOffset, 7, type.titleColor);
            toastGui.getMinecraft().fontRenderer.drawStringWithShadow(this.subtitle, type.textXOffset + 1, 18, type.subtitleColor);
        }

        return delta - this.firstDrawTime < 3000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    /**
     * Changes the text displayed on the notification.
     * @param titleComponent
     * @param subtitleComponent
     */
    public void setDisplayedText(ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent) {
        this.title = titleComponent.getUnformattedText();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getUnformattedText();
        this.newDisplay = true;
    }

    public NotificationToast.Type getType() {
        return this.type;
    }

    /**
     * Displays a device connected toast notification.
     * @param device
     * @return
     */
    public static NotificationToast showConnect(Device device) {
        String title = TEXT_CONNECTED_TITLE.getFormattedText();
        String subtitle = new TextComponentTranslation(TEXT_CONNECTED_SUBTITLE_KEY, device.info.friendlyName).getFormattedText();

        NotificationToast toast = new NotificationToast(Type.DEVICE_CONNECTED, title, subtitle);
        Minecraft.getMinecraft().getToastGui().add(toast);
        return toast;
    }

    /**
     * Displays a device disconnected toast notification.
     * @param device
     * @return
     */
    public static NotificationToast showDisconnect(Device device) {
        String title = TEXT_DISCONNECTED_TITLE.getFormattedText();
        String subtitle = new TextComponentTranslation(TEXT_DISCONNECTED_SUBTITLE_KEY, device.info.friendlyName).getFormattedText();

        NotificationToast toast = new NotificationToast(Type.DEVICE_DISCONNECTED, title, subtitle);
        Minecraft.getMinecraft().getToastGui().add(toast);
        return toast;
    }

    @SideOnly(Side.CLIENT)
    public enum Type {
        DEVICE_CONNECTED(32, 0x00d965, 0x00873C, 28),
        DEVICE_DISCONNECTED(0, -256, -1, 28);
        int textureOffset, titleColor, subtitleColor, textXOffset;

        Type(int textureOffset, int titleColor, int subtitleColor, int textXOffset) {
            this.textureOffset = textureOffset;
            this.titleColor = titleColor;
            this.subtitleColor = subtitleColor;
            this.textXOffset = textXOffset;
        }
    }
}
