package com.logitow.logimine.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Gui for loading structures onto key blocks.
 */
public class LoadStructureGui extends GuiScreen {

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
    private String selectedStructure;

    /**
     * Whether the current page is loading.
     */
    private boolean loading = false;

    /**
     * The currently loaded pages.
     */
    public ArrayList<StructuresPage> loadedPages = new ArrayList<>();

    //Translations
    public final static ITextComponent TEXT_LOAD_SELECTED_STRUCTURE = new TextComponentTranslation("logitow.loadstructuremanager.loadselected");

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
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
        buttonList.add(previousPageButton = new GuiButton(0, width/2 - buttonWidth/2 - buttonSeparation, height/2 + 81, buttonWidth, 20, ">>"));

        //Load bttn
        buttonList.add(loadButton = new GuiButton(1, width/2 - buttonWidth/2, height/2 + 81, buttonWidth, 20, TEXT_LOAD_SELECTED_STRUCTURE.getFormattedText()));

        //Previous bttn
        buttonList.add(nextPageButton = new GuiButton(2, width/2 - buttonWidth/2 + buttonSeparation, height/2 + 81, buttonWidth, 20, "<<"));

        //Going through each of the structures.
        StructuresPage currentPage = loadedPages.get(currentPageId);
        int lastButtonId = 100;
        int lastButtonHeight = getListStartPosition();
        int verticalSeparation = 5;
        int structureButtonWidth = 100;
        int structureButtonHeight = 20;
        if(currentPage != null) {
            //Hiding the loading message.
            loading = false;

            for (String structure :
                    currentPage.structures) {
                if(structure.contains("^")) {
                    //Custom name
                    buttonList.add(new GuiButton(lastButtonId, width/2 - structureButtonWidth/2, lastButtonHeight, structureButtonWidth, structureButtonHeight, structure.split("^")[0]));
                } else {
                    //UUID
                    buttonList.add(new GuiButton(lastButtonId, width/2 - structureButtonWidth/2, lastButtonHeight, structureButtonWidth, structureButtonHeight, structure));
                }

                lastButtonId++;
                lastButtonHeight += structureButtonHeight/2 + verticalSeparation;
            }
        } else {
            //Showing loading text
            loading = true;

            //Next page not available.
            nextPageButton.enabled = false;

            //Checking previous page.
            if(loadedPages.get(currentPageId-1) != null) {
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
        //Checking if the page is loaded.
        currentPageId = page;
        StructuresPage loadedPage = loadedPages.get(page);

        if(loadedPage == null) {
            //Requesting the page.
            requestLoadPage(page);
        }

        //Updating buttons.
        updateButtons();
    }

    /**
     * Requests the loading of a page.
     * @param id
     */
    private void requestLoadPage(int id) {

    }

    /**
     * Loads the currently selected
     */
    public void loadSelected() {
        //TODO
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

    /**
     * Represents a single page of loadable structures.
     */
    public class StructuresPage {
        /**
         * Id of the page.
         */
        public int id;

        /**
         * Whether the next page is available.
         */
        public boolean nextAvailable;
        /**
         * Whether the previous page is available.
         */
        public boolean previousAvailable;

        /**
         * The structures on the list.
         */
        public ArrayList<String> structures = new ArrayList<>();
    }
}
