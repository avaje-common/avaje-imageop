package org.avaje.imageop.processor;

/**
 * Mode used to convert an image.
 * <p>
 * Typically Max is used for main images and any of the 3 are used for thumbnail images.
 */
public enum ConvertMode {

  /**
   * Resize the image using the width and height as maximum values keeping the original image aspect
   * ratio.
   */
  Max(false, false),

  /**
   * Crop part of the image to fit the width and height.
   */
  Crop(false, true),
  
  /**
   * Pad the image with a background color. This preserves the aspect ratio and ensures the
   * converted image is the defined width and height.
   */
  Pad(true, true), 
  
  /**
   * A halfway between Pad and Crop. This will perform some padding and some cropping based on
   * the area. This is likely to be better than just Pad.
   */
  PadArea(true, true);
  
  private boolean background;
  private boolean extent;
  
  ConvertMode(boolean background, boolean extent) {
    this.background = background;
    this.extent = extent;
  }
  
  public boolean hasBackground() {
    return background;
  }
  
  public boolean hasExtent() {
    return extent;
  }
  
}