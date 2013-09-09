package org.avaje.imageop.processor;

import java.io.File;

/**
 * Details for an image file.
 */
public class ImageFileDetail {

  /**
   * The file name.
   */
  String name;
  
  /**
   * The file extension.
   */
  String extension;
  
  /**
   * The image width.
   */
  int width;
  
  /**
   * The image height.
   */
  int height;
  
  /**
   * The image size in bytes.
   */
  long length;

  /**
   * The file for this image.
   */
  File file;

  public boolean deleteFile() {
    if (file != null){
      return file.delete();
    }
    return true;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }
}
