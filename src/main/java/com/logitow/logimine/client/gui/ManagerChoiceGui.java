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

import javax.annotation.Nullable;

/**
 * GUI for choosing between LOGITOW managers.
 * TODO: Actually implement as a functionality.
 */
public class ManagerChoiceGui extends GuiScreen {

    public static ManagerChoiceGui instance;

    /**
     * The currently selected key block to attach devices to.
     * NULL if no block selected.
     */
    private static TileEntityBlockKey selectedKeyBlock = null;

    public final int DEVICE_MANAGER_BUTTON_ID = 0;
    public ManagerButton deviceManagerButton;
    public final int STRUCTURE_MANAGER_BUTTON_ID = 1;
    public ManagerButton structureManagerButton;

    final public ResourceLocation deviceManagerButtonTexture = new ResourceLocation(LogiMine.modId, "gui/device-manager-button.png");
    final public ResourceLocation structureManagerButtonTexture = new ResourceLocation(LogiMine.modId, "gui/structure-manager-button.png");

    final public TextComponentTranslation TEXT_DEVICE_MANAGER = new TextComponentTranslation("logitow.managerchoice.devicemanager");
    final public TextComponentTranslation TEXT_STRUCTURE_MANAGER = new TextComponentTranslation("logitow.managerchoice.structuremanager");

    int buttonWidth = 256;
    int buttonHeight = 256;

    @Override
    public void initGui() {
        //Setting up buttons.
        instance = this;
        buttonList.clear();
        int buttonSeparation = 140;

        //Device manager bttn
        float scale1 = .4f;
        buttonList.add(deviceManagerButton = new ManagerButton(DEVICE_MANAGER_BUTTON_ID, this.width/2 + (int)(buttonWidth/2*scale1) + 50 + 90 - buttonSeparation, height/2 + (int)((buttonHeight*scale1)/2), buttonWidth, buttonHeight, "", deviceManagerButtonTexture,scale1));

        //Structure manager bttn
        float scale2 = .44f;
        buttonList.add(structureManagerButton = new ManagerButton(STRUCTURE_MANAGER_BUTTON_ID, this.width/2 + (int)(buttonWidth/2*scale2) + 90 + buttonSeparation, height/2 + (int)((buttonHeight*scale2)/2) -30, buttonWidth, buttonHeight,"", structureManagerButtonTexture,scale2));

        //Cancel bttn
        //TODO
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
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

        //Drawing managers labels.
        String deviceManager = TEXT_DEVICE_MANAGER.getFormattedText();
        drawString(fontRenderer, deviceManager, deviceManagerButton.x + deviceManagerButton.width/2 - fontRenderer.getStringWidth(deviceManager)/2, (height/3), 0x00ff00);

        String structureManager = TEXT_STRUCTURE_MANAGER.getFormattedText();
        drawString(fontRenderer, structureManager, structureManagerButton.x + structureManagerButton.width/2 - fontRenderer.getStringWidth(structureManager)/2, (height/3), 0x00ff00);
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

            System.out.println("Set the key block reference to: " + ManagerChoiceGui.instance.getSelectedKeyBlock().getPos());
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
            }
        }

        public int getId()
        {
            return this.id;
        }
    }
}
