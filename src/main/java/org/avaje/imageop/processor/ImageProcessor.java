package org.avaje.imageop.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.avaje.imageop.filter.CropScaleImageOp;
import org.avaje.imageop.filter.ImageOp;
import org.avaje.imageop.filter.MaxSizeImageOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to process images using Crop, Scale and Max operations into a expected image size.
 * <p>
 * Provides the ability to generate Thumbnail images as part of the process.
 * </p>
 */
public class ImageProcessor {

  private static final Logger log = LoggerFactory.getLogger(ImageProcessor.class);
  
  /**
   * Mode used to process the main image.
   */
  public enum Mode {
    
    /**
     * Scale and Crop the image to the width and height.
     */
    ScaleCrop,
    
    /**
     * Use the width and height as maximum values keeping the original image width/height ratio.
     */
    Max
  }
  
  /**
   * The Mode used to control the main image processing strategy.
   */
  private final Mode mode;
  
  /**
   * The main image width.
   */
  private final int width;
  
  /**
   * The main image height.
   */
  private final int height;

  /**
   * The Thumbnail image width.
   */
  private final int thumbWidth;
  
  /**
   * The Thumbnail image height.
   */
  private final int thumbHeight;

  private final File tempDirectory;
  
  private ImageOp thumbImageOp;
  
  private ImageOp mainImageOp;
  
  /**
   * Create the ImageProcessor with no thumbnail and using Mode.ScaleCrop and system temporary directory.
   */
  public ImageProcessor(int width, int height) {
    this(0, 0, width, height, Mode.ScaleCrop, null);
  }

  /**
   * Create the ImageProcessor using Mode.ScaleCrop and system temporary directory.
   */
  public ImageProcessor(int thumbWidth, int thumbHeight, int width, int height) {
    this(thumbWidth, thumbHeight, width, height, Mode.ScaleCrop, null);
  }
  
  /**
   * Create the ImageProcessor with thumbnail and scaled image sizes.
   * 
   * @param thumbWidth the thumbnail image width
   * @param thumbHeight the thumbnail image height
   * @param width the main image width
   * @param height the main image height
   * @param tempDirectory the temporary directory used when processing the images
   */
  public ImageProcessor(int thumbWidth, int thumbHeight, int width, int height, Mode mode, File tempDirectory) {

    this.thumbWidth = thumbWidth;
    this.thumbHeight = thumbHeight;
    this.width = width;
    this.height = height;
    this.mode = (mode == null) ? Mode.ScaleCrop : mode;
    this.tempDirectory = tempDirectory;

    this.thumbImageOp = new CropScaleImageOp(thumbWidth, thumbHeight);
    this.mainImageOp = initMainFilter();
  }

  /**
   * Return the ImageOp used to process the main image.
   */
  protected ImageOp initMainFilter() {

    if (Mode.Max.equals(mode)) {
      return new MaxSizeImageOp(width, height);
    }    
    return new CropScaleImageOp(width, height);
  }

  
  /**
   * Process the file scaling and cropping as required producing a thumbnail and scaled version of the image.
   * The ImageSet returned contains the original image file and thumbnail and scaled versions of the image.
   */
  public ImageFileSet process(File originalFile, String uploadFileName) throws IOException {

    InputStream is = new FileInputStream(originalFile);
    try {
      return process(is, uploadFileName);
    } finally {
      is.close();
    }
  }    
  
  /**
   * Process the file scaling and cropping as required producing a thumbnail and scaled version of the image.
   * The ImageSet returned contains the original image file and thumbnail and scaled versions of the image.
   */
  public ImageFileSet process(InputStream originalFile, String sourceFileName) throws IOException {

    log.debug("processing {}", sourceFileName);

    int lastDot = sourceFileName.lastIndexOf('.');
    String sourceExtension = sourceFileName.substring(lastDot + 1).toLowerCase();
    
    String sourceName = sourceFileName.substring(0, lastDot);
    int lastSlash = sourceName.lastIndexOf('/');
    if (lastSlash > -1) {
      sourceName = sourceName.substring(lastSlash);
    }
    int lastBackSlash = sourceName.lastIndexOf('\\');
    if (lastBackSlash > -1) {
      sourceName = sourceName.substring(lastBackSlash);
    }
    

    BufferedImage in = ImageIO.read(originalFile);

    ImageFileDetail maxImage = null;
    if (width > 0 && height > 0) {
      ImageOp mainFilter = getMainFilter();
      String mainFileName = "-main" + width + "x" + height + "-";
      maxImage = processFilter(in, mainFilter, sourceName, sourceExtension, mainFileName);
    }

    ImageFileDetail thumbImage = null;
    if (thumbWidth > 0 && thumbHeight > 0) {
      ImageOp thumbFilter = getThumbFilter();
      String thumbFileName = "-thumb" + thumbWidth + "x" + thumbHeight + "-";

      thumbImage = processFilter(in, thumbFilter, sourceName, sourceExtension, thumbFileName);
    }

    return new ImageFileSet(sourceName, sourceExtension, thumbImage, maxImage);
  }
  
  public ImageOp getThumbImageOp() {
    return thumbImageOp;
  }

  public void setThumbImageOp(ImageOp thumbImageOp) {
    this.thumbImageOp = thumbImageOp;
  }

  public ImageOp getMainImageOp() {
    return mainImageOp;
  }

  public void setMainImageOp(ImageOp mainImageOp) {
    this.mainImageOp = mainImageOp;
  }

  /**
   * Return the ImageOp used to process the main image.
   */
  protected ImageOp getMainFilter() {
    return mainImageOp;
  }

  /**
   * Return the ImageOp used to process the thumbnail image.
   */
  protected ImageOp getThumbFilter() {
    return thumbImageOp;
  }

  /**
   * Process the image input returning the ImageFileDetail.
   */
  protected ImageFileDetail processFilter(BufferedImage in, ImageOp filter,
       String sourceName, String extension, String tmpFileName) throws IOException {

    BufferedImage thumbBufferedImage = filter.filter(in, null);

    File thumbFile = File.createTempFile(sourceName+tmpFileName, "."+extension, tempDirectory);
    ImageIO.write(thumbBufferedImage, extension, thumbFile);

    return createImageFileDetail(thumbBufferedImage, tmpFileName, extension, thumbFile);
  }

  protected ImageFileDetail createImageFileDetail(BufferedImage bufferedImage, String name, String extn, File file) {
    
    ImageFileDetail i = new ImageFileDetail();
    i.setName(name);
    i.setExtension(extn);
    i.setWidth(bufferedImage.getWidth());
    i.setHeight(bufferedImage.getHeight());
    i.setLength(file.length());
    i.setFile(file);
    return i;
  }
}

