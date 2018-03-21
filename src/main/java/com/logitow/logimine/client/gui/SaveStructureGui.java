package com.logitow.logimine.client.gui;

import com.logitow.logimine.LogiMine;
import com.logitow.logimine.networking.LogitowSaveStructureMessage;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents the save structures GUI window.
 */
@SideOnly(Side.CLIENT)
public class SaveStructureGui extends GuiScreen {
    /**
     * The location of the gui texture.
     */
    final public ResourceLocation guiTexture = new ResourceLocation(LogiMine.modId, "gui/structure-save-manager.png");

    private ArrayList<GuiTextField> textFields = new ArrayList<>();

    private boolean wrongName = false;

    /**
     * The height of the container graphic.
     */
    final int containerHeight = 104;
    /**
     * The width of the container graphic.
     */
    final int containerWidth = 122;

    /**
     * The currently used keyblock.
     */
    private TileEntityBlockKey keyBlock;

    public SaveStructureGui(TileEntityBlockKey blockKey) {
        this.keyBlock = blockKey;
    }

    //Text
    public static final ITextComponent TEXT_SAVE_STRUCTURE_MANAGER_TITLE = new TextComponentTranslation("logitow.savestructuremanager.title");
    public static final ITextComponent TEXT_SAVE_STRUCTURE_MANAGER_SAVE_BUTTON = new TextComponentTranslation("logitow.savestructuremanager.savebutton");
    public static final ITextComponent TEXT_SAVE_STRUCTURE_MANAGER_CANCEL_BUTTON = new TextComponentTranslation("logitow.savestructuremanager.cancelbutton");
    public static final ITextComponent TEXT_SAVE_STRUCTURE_MANAGER_SAVING = new TextComponentTranslation("logitow.savestructuremanager.saving");

    //Buttons
    final int SAVE_BUTTON_ID = 0;
    final int CANCEL_BUTTON_ID = 1;
    final int NAME_FIELD_ID = 2;

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        this.drawDefaultBackground();
        Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);

        //Getting the center coords.
        int centerX = (width/2) - containerWidth/2;
        int centerY = (height/2) - containerHeight/2;

        drawTexturedModalRect(centerX,centerY,0,0,containerWidth,containerHeight);
        String title = TEXT_SAVE_STRUCTURE_MANAGER_TITLE.getFormattedText();
        drawString(fontRenderer, title, (width/2) - fontRenderer.getStringWidth(title)/2, (height/2) - 6, 0x00ff00);

        for (GuiTextField textField :
                textFields) {
            textField.drawTextBox();
        }

        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        System.out.println("Action performed!");

        if(button.id == CANCEL_BUTTON_ID) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        if (button.id == SAVE_BUTTON_ID) {
            //Checking the name.
            String name = textFields.get(0).getText();
            if(name != null && name != "") {
                //Sending save request.
                System.out.println("Sending save request: " + FMLCommonHandler.instance().getEffectiveSide());
                LogiMine.networkWrapper.sendToServer(new LogitowSaveStructureMessage(this.keyBlock.getPos(), name));
                Minecraft.getMinecraft().displayGuiScreen(null);
                Minecraft.getMinecraft().player.sendMessage(TEXT_SAVE_STRUCTURE_MANAGER_SAVING);
                return;
            } else {
                wrongName = true;
                return;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        //Adding buttons.
        buttonList.clear();
        int buttonSeparation = 30;
        int buttonWidth = 50;

        //Save bttn
        buttonList.add(new GuiButton(SAVE_BUTTON_ID, width/2 - buttonWidth/2 - buttonSeparation, height/2 + 28, buttonWidth, 20, TEXT_SAVE_STRUCTURE_MANAGER_SAVE_BUTTON.getFormattedText()));

        //Cancel bttn
        buttonList.add(new GuiButton(CANCEL_BUTTON_ID, width/2 - buttonWidth/2 + buttonSeparation, height/2 + 28, buttonWidth, 20, TEXT_SAVE_STRUCTURE_MANAGER_CANCEL_BUTTON.getFormattedText()));

        //Adding text fields.
        int textFieldWidth = 100;
        GuiTextField field;
        textFields.add(field = new GuiTextField(NAME_FIELD_ID, fontRenderer, width/2 - textFieldWidth/2, height/2 + 7, textFieldWidth, 15));
        field.setEnabled(true);
        field.setFocused(true);
        field.setCursorPosition(0);

        Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();

        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTextField textField :
                textFields) {
            if(textField != null) {
                textField.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (GuiTextField textField :
                textFields) {
            if (textField.isFocused()) {
                textField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }
}
