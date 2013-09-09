package org.avaje.imageop.processor;



/**
 *
 */
public class ImageFileSet {

  private final String sourceName;
  private final String sourceExtension;
  
  private final ImageFileDetail thumbImage;
  private final ImageFileDetail normalImage;

  public ImageFileSet(String sourceName, String sourceExtension, ImageFileDetail thumbImage, ImageFileDetail normalImage) {
    this.sourceName = sourceName;
    this.sourceExtension = sourceExtension;
    this.normalImage = normalImage;
    this.thumbImage = thumbImage;
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
}
