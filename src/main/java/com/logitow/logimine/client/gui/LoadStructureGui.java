package com.logitow.logimine.client.gui;

import com.logitow.logimine.LogiMine;
import com.logitow.logimine.networking.LogitowLoadStructureMessage;
import com.logitow.logimine.networking.LogitowStructureListRequestMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Gui for loading structures onto key blocks.
 */
public class LoadStructureGui extends GuiScreen {

    /**
     * Currently open load gui.
     */
    public static LoadStructureGui instance;

    /**
     * The location of the gui texture.
     */
    final public ResourceLocation guiTexture = new ResourceLocation(LogiMine.modId, "gui/tall-manager.png");

    /**
     * Button to load the currently selected structures.
     */
    private GuiButton loadButton;

    /**
     * Button to switch to next page.
     */
    private GuiButton nextPageButton;

    /**
     * Button to switch to previous page.
     */
    private GuiButton previousPageButton;

    /**
     * The currently opened page.
     */
    private int currentPageId = 0;

    /**
     * The currently selected structure.
     */
    private int selectedStructure = -1;

    /**
     * Whether the current page is loading.
     */
    private boolean loading = false;

    /**
     * The height of the container graphic.
     */
    final int containerHeight = 256;
    /**
     * The width of the container graphic.
     */
    final int containerWidth = 162;

    /**
     * The currently loaded pages.
     */
    public ArrayList<StructuresPage> loadedPages = new ArrayList<>();

    //Translations
    public final static ITextComponent TEXT_LOAD_SELECTED_STRUCTURE = new TextComponentTranslation("logitow.loadstructuremanager.loadselected");
    public final static ITextComponent TEXT_LOAD_STRUCTURE_TITLE = new TextComponentTranslation("logitow.loadstructuremanager.title");
    public final static ITextComponent TEXT_LOAD_STRUCTURE_SUBTITLE = new TextComponentTranslation("logitow.loadstructuremanager.subtitle");
    public final static ITextComponent TEXT_LOAD_PAGE_LOADING = new TextComponentTranslation("logitow.loadstructuremanager.pageloading");
    public final static String TEXT_LOADING_STRUCTURE_KEY = "logitow.loadstructuremanager.loading";

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Head
        drawDefaultBackground();
        Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);

        //Getting the center coords.
        int centerX = (width/2) - containerWidth/2;
        int centerY = (height/2) - containerHeight/2;

        drawTexturedModalRect(centerX,centerY,0,0,containerWidth,containerHeight);
        String title = TEXT_LOAD_STRUCTURE_TITLE.getFormattedText();
        drawString(fontRenderer, title, (width/2) - fontRenderer.getStringWidth(title)/2, (height/2) - containerHeight/3 +5, 0x00ff00);

        //Subtitle
        String availableDevices = TEXT_LOAD_STRUCTURE_SUBTITLE.getFormattedText();
        drawString(fontRenderer, availableDevices, (width/2) - fontRenderer.getStringWidth(availableDevices)/2, (height/2) - containerHeight/3 +17, 0x8b8b8b);

        //Drawing the scanning string.
        if(loading) {
            String scanning = TEXT_LOAD_PAGE_LOADING.getFormattedText();
            drawString(fontRenderer, scanning, (width/2) - fontRenderer.getStringWidth(scanning)/2, (height/2) + 1, 0x8b8b8b);
        }

        //Checking button enabling.
        for (GuiButton button :
                buttonList) {
            if (button.id >= 100) {
                int id = button.id-100;
                if(id == selectedStructure) {
                    button.enabled = false;
                } else {
                    button.enabled = true;
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        instance = this;
        requestLoadPage(currentPageId);
        updateButtons();
        super.initGui();
    }

    /**
     * Called when a page is loaded from the server.
     * @param page
     */
    public void onPageLoaded(StructuresPage page) {
        loadedPages.add(page);
        if(page.id == currentPageId) {
            updateButtons();
        }
    }

    /**
     * Updates the buttons currently on screen.
     */
    private void updateButtons() {
        //Clearing the current buttons.
        buttonList.clear();

        //Setting up buttons.
        int buttonSeparation = 50;
        int buttonWidth = 50;

        //Next bttn
        buttonList.add(previousPageButton = new GuiButton(0, width/2 - buttonWidth/2 - buttonSeparation, height/2 + 81, buttonWidth, 20, "<<"));

        //Load bttn
        buttonList.add(loadButton = new GuiButton(1, width/2 - buttonWidth/2, height/2 + 81, buttonWidth, 20, TEXT_LOAD_SELECTED_STRUCTURE.getFormattedText()));

        //Previous bttn
        buttonList.add(nextPageButton = new GuiButton(2, width/2 - buttonWidth/2 + buttonSeparation, height/2 + 81, buttonWidth, 20, ">>"));

        //Going through each of the structures.
        StructuresPage currentPage;
        if(loadedPages.size() < currentPageId + 1) {
            currentPage = null;
        } else {
            currentPage= loadedPages.get(currentPageId);
        }
        int lastButtonHeight = getListStartPosition();
        int verticalSeparation = 11;
        int structureButtonWidth = 138;
        int structureButtonHeight = 20;
        if(currentPage != null) {
            //Hiding the loading message.
            loading = false;

            for (int i = 0; i<currentPage.structures.size();i++) {
                String structure = getStructureNameFormatted(currentPage.structures.get(i));
                buttonList.add(new GuiButton(100+i, width/2 - structureButtonWidth/2, lastButtonHeight, structureButtonWidth, structureButtonHeight, structure));

                lastButtonHeight += structureButtonHeight/2 + verticalSeparation;
            }

            //Checking the next page.
            if(currentPageId + 1 >= 0 && loadedPages.size() >= currentPageId+2 && loadedPages.get(currentPageId+1) != null) {
                nextPageButton.enabled = true;
            } else {
                nextPageButton.enabled = false;
            }

            //Checking previous page.
            if(currentPageId - 1 >= 0 && loadedPages.size() >= currentPageId && loadedPages.get(currentPageId-1) != null) {
                previousPageButton.enabled = true;
            } else {
                previousPageButton.enabled = false;
            }
        } else {
            //Showing loading text
            loading = true;

            //Next page not available.
            nextPageButton.enabled = false;

            //Checking previous page.
            if(currentPageId - 1 != -1 && loadedPages.size() >= currentPageId && loadedPages.get(currentPageId-1) != null) {
                previousPageButton.enabled = true;
            } else {
                previousPageButton.enabled = false;
            }
        }
    }

    /**
     * Switches the current page to the given id.
     * @param page
     */
    public void switchPage(int page) {
        if(page <0) return;
        //Checking if the page is loaded.
        currentPageId = page;
        StructuresPage loadedPage;
        if(loadedPages.size() < currentPageId + 1) {
            loadedPage = null;
        } else {
            loadedPage = loadedPages.get(page);
        }

        if(loadedPage == null) {
            //Requesting the page.
            requestLoadPage(page);
        }

        selectedStructure = -1;

        //Updating buttons.
        updateButtons();
    }

    /**
     * Requests the loading of a page.
     * @param id
     */
    private void requestLoadPage(int id) {
        System.out.println("Requesting page: " + id);
        LogiMine.networkWrapper.sendToServer(new LogitowStructureListRequestMessage(id));
    }

    /**
     * Loads the currently selected
     */
    public void loadSelected() {
        StructuresPage currPage = loadedPages.get(currentPageId);
        if(selectedStructure >= 0 && currPage != null) {
            String structure = currPage.structures.get(selectedStructure);
            LogiMine.networkWrapper.sendToServer(new LogitowLoadStructureMessage(HubGui.getSelectedKeyBlock().getPos(), structure));
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(TEXT_LOADING_STRUCTURE_KEY, getStructureNameFormatted(structure)));
        }
    }

    /**
     * Formats the name of a structure.
     * @param unformatted
     * @return
     */
    private String getStructureNameFormatted(String unformatted) {
        if(unformatted.contains("^")) {
            return unformatted.split("\\^")[0];
        } else {
            return unformatted;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        //Load button.
        switch (button.id) {
            case 0:
                //Next button
                switchPage(currentPageId+1);
                break;
            case 1:
                //Load button
                loadSelected();
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 2:
                //Previous button
                switchPage(currentPageId-1);
                break;
        }

        if(button.id >= 100) {
            selectedStructure = button.id-100;
        }

        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * The list start y coordinate.
     */
    private int getListStartPosition() {
        return height/2 - 50;
    }

    @Override
    public void onGuiClosed() {
        instance = null;
        super.onGuiClosed();
    }
}
