package de.tud.stg.tigerseye.core;

public enum TigerseyeImage {
    
    RunTigerseye("plane16.png"), //
    DebugTigerseye("plane16.png"), //
    FileTypeTigerseye("plane16.png"), //
    ;
    
    public String imageName;

    private TigerseyeImage(String imageName) {
	this.imageName = imageName;
    }
}

