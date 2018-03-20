package com.logitow.logimine.client.gui;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a single page of loadable structures.
 */
public class StructuresPage implements Serializable {
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