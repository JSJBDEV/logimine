package com.logitow.logimine.client.gui;

import com.logitow.logimine.LogiMine;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * GUI for choosing between LOGITOW managers.
 */
@SideOnly(Side.CLIENT)
public class HubGui extends GuiScreen {

    public static HubGui instance;

    /**
     * The currently selected key block to attach devices to.
     * NULL if no block selected.
     */
    private static TileEntityBlockKey selectedKeyBlock = null;

    public final int DEVICE_MANAGER_BUTTON_ID = 0;
    public ManagerButton deviceManagerButton;
    public final int STRUCTURE_MANAGER_BUTTON_ID = 1;
    public ManagerButton structureManagerButton;
    public final int DEVICE_MANAGER_BUTTON2_ID = 2;
    public GuiButton deviceManagerButton2;
    public final int STRUCTURE_MANAGER_BUTTON2_ID = 3;
    public GuiButton structureManagerButton2;

    int scaleTicks = 0;
    boolean tickUp = true;

    final public ResourceLocation deviceManagerButtonTexture = new ResourceLocation(LogiMine.modId, "gui/device-manager-button.png");
    final public ResourceLocation structureManagerButtonTexture = new ResourceLocation(LogiMine.modId, "gui/structure-manager-button.png");

    final public TextComponentTranslation TEXT_DEVICE_MANAGER = new TextComponentTranslation("logitow.managerchoice.devicemanager");
    final public TextComponentTranslation TEXT_STRUCTURE_MANAGER = new TextComponentTranslation("logitow.managerchoice.structuremanager");
    final public TextComponentTranslation TEXT_HUB_SUBTITLE = new TextComponentTranslation("logitow.hub.subtitle");
    final public String TEXT_HUB_CURRENT_DEVICE_KEY = "logitow.hub.connecteddevice";
    final public String TEXT_HUB_CURRENT_DEVICE_BATTERY_KEY ="logitow.hub.connecteddevice.battery";

    int buttonWidth = 256;
    int buttonHeight = 256;

    @Override
    public void initGui() {
        //Setting up buttons.
        instance = this;
        buttonList.clear();
        int buttonSeparation = 170;

        //Device manager bttn
        float scale1 = .4f;
        buttonList.add(deviceManagerButton = new ManagerButton(DEVICE_MANAGER_BUTTON_ID, (int)(this.width/scale1/2) - buttonWidth/2 - buttonSeparation -5, height/2 + (int)((buttonHeight*scale1)/2) + height/10, buttonWidth, buttonHeight, "", deviceManagerButtonTexture,scale1));

        //Structure manager bttn
        float scale2 = .44f;
        buttonList.add(structureManagerButton = new ManagerButton(STRUCTURE_MANAGER_BUTTON_ID, (int)(this.width/scale2/2) - buttonWidth/2 + buttonSeparation -5, height/2 + (int)((buttonHeight*scale2)/2) -30 + height/10, buttonWidth, buttonHeight,"", structureManagerButtonTexture,scale2));

        //Drawing managers labels.
        int smallButtonHeight = 20;
        int smallButtonWidth = 110;
        int smallButtonSeparation = 70;
        String deviceManager = TEXT_DEVICE_MANAGER.getFormattedText();
        buttonList.add(deviceManagerButton2 = new GuiButton(DEVICE_MANAGER_BUTTON2_ID, this.width/2 - smallButtonWidth/2 - smallButtonSeparation, height/2 - smallButtonHeight/2 + height/3 -15, smallButtonWidth, smallButtonHeight, deviceManager));

        String structureManager = TEXT_STRUCTURE_MANAGER.getFormattedText();
        buttonList.add(structureManagerButton2 = new GuiButton(STRUCTURE_MANAGER_BUTTON2_ID, this.width/2 - smallButtonWidth/2 + smallButtonSeparation, height/2 - smallButtonHeight/2 + height/3 -15, smallButtonWidth, smallButtonHeight, structureManager));

        //Requesting battery update if the keyblock is selected.
        if(getSelectedKeyBlock() != null && getSelectedKeyBlock().getAssignedDevice() != null) {
            System.out.println("Requesting battery update from " + getSelectedKeyBlock().getAssignedDevice());
            getSelectedKeyBlock().getAssignedDevice().requestBatteryInfoUpdate();
        }

        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        System.out.println("Action!");
        Minecraft.getMinecraft().displayGuiScreen(null);

        if(button.id == 0 || button.id == 2) {
            Minecraft.getMinecraft().displayGuiScreen(new DeviceManagerGui());
        }
        if(button.id == 1 || button.id == 3) {
            Minecraft.getMinecraft().displayGuiScreen(new LoadStructureGui());
        }
        super.actionPerformed(button);
    }

    /**
     * Draws the gui.
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        //Rendering manager buttons.
        for (GuiButton button :
                this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        //Rendering the subtitle text.
        String subtitle = TEXT_HUB_SUBTITLE.getFormattedText();
        fontRenderer.drawString(subtitle, width/2 - (fontRenderer.getStringWidth(subtitle)/2), height/2 - height/3.7f, 0xffffff, true);

        if(getSelectedKeyBlock() != null && getSelectedKeyBlock().getAssignedDevice() != null) {
            //Rendering the device name and battery.
            fontRenderer.drawString(new TextComponentTranslation(TEXT_HUB_CURRENT_DEVICE_KEY, getSelectedKeyBlock().getAssignedDevice().toString()).getFormattedText(), 5, 5, 0xffffff);
            if(getSelectedKeyBlock().getAssignedDevice().deviceBattery != null) {
                fontRenderer.drawString(new TextComponentTranslation(TEXT_HUB_CURRENT_DEVICE_BATTERY_KEY, (int)(getSelectedKeyBlock().getAssignedDevice().deviceBattery.getChargePercent()*100) + "%").getFormattedText(), 5, 15, 0xffffff);
            }
        }

        //Rendering the LOGITOW text.
        String logitowText = "LOGITOW";
        float scaleAdd = scaleTicks*0.02f;
        if(scaleTicks >= 50) {
            tickUp = false;
        }
        if(scaleTicks <= 0) {
            tickUp = true;
        }
        if(tickUp) {
            scaleTicks++;
        } else {
            scaleTicks--;
        }
        float baseScale = 2f;
        GL11.glScalef(baseScale + scaleAdd,baseScale + scaleAdd,1f);
        fontRenderer.drawString(logitowText, width/2/(baseScale+scaleAdd) - (fontRenderer.getStringWidth(logitowText)/2), height/2/(baseScale+scaleAdd) - height/2.5f/(baseScale+scaleAdd), 0xff7535, true);
    }

    /**
     * Called when the selected key block is resolved.
     * @param pos
     */
    public static void setSelectedKeyBlock(@Nullable BlockPos pos) {
        //Assigning the key block.
        if(pos != null) {
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
            if(te == null || !(te instanceof TileEntityBlockKey)) return;
            selectedKeyBlock = (TileEntityBlockKey)te;

            System.out.println("Set the key block reference to: " + HubGui.instance.getSelectedKeyBlock().getPos());
        } else {
            selectedKeyBlock = null;
        }
    }

    /**
     * Gets the selected key block.
     * @return
     */
    public static TileEntityBlockKey getSelectedKeyBlock() {
        return selectedKeyBlock;
    }

    /**
     * Custom button for choosing manager.
     */
    public class ManagerButton extends GuiButton
    {
        /**
         * Location of the button texture.
         */
        private ResourceLocation textureLocation;
        /**
         * Scale factor for the button texture.
         */
        private float scaleFactor;

        public ManagerButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, ResourceLocation textureLocation, float scaleFactor) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.textureLocation = textureLocation;
            this.scaleFactor = scaleFactor;
            this.visible = true;
            this.width = widthIn;
            this.height = heightIn;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if(this.visible) {
                GlStateManager.pushMatrix();
                mc.renderEngine.bindTexture(this.textureLocation);
                GlStateManager.scale(this.scaleFactor, this.scaleFactor, 1.0);
                this.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, this.width, this.height, 256, 256);
                GlStateManager.popMatrix();
                this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);
            }
        }

        public int getId()
        {
            return this.id;
        }
    }
}
