package org.avaje.imageop.processor;



/**
 * Set of image details potentially containing a thumbnail image, scaled image and the original image.
 */
public class ImageFileSet {

  private final String sourceName;
  private final String sourceExtension;
  
  private final ImageFileDetail thumbImage;
  private final ImageFileDetail normalImage;
  private final ImageFileDetail originalImage;

  public ImageFileSet(String sourceName, String sourceExtension, ImageFileDetail thumbImage, ImageFileDetail normalImage, ImageFileDetail originalImage) {
    this.sourceName = sourceName;
    this.sourceExtension = sourceExtension;
    this.normalImage = normalImage;
    this.thumbImage = thumbImage;
    this.originalImage = originalImage;
  }

  public boolean deleteFiles() {
    boolean deleteOk = true;
    if (normalImage != null){
      deleteOk &= normalImage.deleteFile();
    }
    if (thumbImage != null){
      deleteOk &= thumbImage.deleteFile();
    }
    return deleteOk;
  } 

  public String getSourceName() {
    return sourceName;
  }

  public String getSourceExtension() {
    return sourceExtension;
  }

  public ImageFileDetail getThumbImage() {
    return thumbImage;
  }

  public ImageFileDetail getNormalImage() {
    return normalImage;
  }

  public ImageFileDetail getOriginalImage() {
    return originalImage;
  }
  
}
