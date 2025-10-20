package com.readapt.ui.components;

import javafx.scene.layout.Region;

public class SectionDivider extends Region {
    public SectionDivider() {
        this.setMinHeight(1);
        this.setPrefHeight(1);
        this.setMaxHeight(1);
        this.getStyleClass().add("section-divider");
    }
}