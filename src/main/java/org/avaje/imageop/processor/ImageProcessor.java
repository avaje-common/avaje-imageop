package org.avaje.imageop.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
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
   * The Mode used to control the thumbnail image processing strategy.
   */
  private final ConvertMode thumbMode;
  
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
  
  private String thumbnailBackground = "transparent";

  /**
   * If set this means all thumbnails are converted to this image format.
   * You might set this to GIF for smaller thumnails.
   */
  private String thumbnailExtension;

  /**
   * The default main image format when it is not a png or jpg image (like a tiff).
   */
  private String defaultMainImageExtension = "jpg";
  
  /**
   * Create the ImageProcessor with no thumbnail and system temporary directory.
   */
  public ImageProcessor(int width, int height) {
    this(0, 0, width, height, null, null);
  }

  /**
   * Create the ImageProcessor using ConvertMode.Crop and system temporary directory.
   */
  public ImageProcessor(int thumbWidth, int thumbHeight, int width, int height) {
    this(thumbWidth, thumbHeight, width, height, null, null);
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
  public ImageProcessor(int thumbWidth, int thumbHeight, int width, int height, ConvertMode thumbMode, File tempDirectory) {

    this.thumbWidth = thumbWidth;
    this.thumbHeight = thumbHeight;
    this.width = width;
    this.height = height;
    this.thumbMode = (thumbMode == null) ? ConvertMode.Crop : thumbMode;
    this.tempDirectory = tempDirectory;
  }

  
  /**
   * Process the file scaling and cropping as required producing a thumbnail and scaled version of the image.
   * The ImageSet returned contains the original image file and thumbnail and scaled versions of the image.
   */
  public ImageFileSet process(File originalFile, String uploadFileName) throws IOException {      
  
    log.debug("processing {}", uploadFileName);

    int lastDot = uploadFileName.lastIndexOf('.');
    String sourceExtension = uploadFileName.substring(lastDot + 1).toLowerCase();
    
    String sourceName = uploadFileName.substring(0, lastDot);
    int lastSlash = sourceName.lastIndexOf('/');
    if (lastSlash > -1) {
      sourceName = sourceName.substring(lastSlash);
    }
    int lastBackSlash = sourceName.lastIndexOf('\\');
    if (lastBackSlash > -1) {
      sourceName = sourceName.substring(lastBackSlash);
    }
    
    InputStream originalStream = new FileInputStream(originalFile);
    try {
  
      ImageFileDetail maxImage = null;
      if (width > 0 && height > 0) {
        // convert the main image
        String mainExtn = deriveExtension(sourceExtension);
        String mainFileName = sourceName+"-main" + width + "x" + height + "-";
        File mainFile = File.createTempFile(mainFileName, "."+mainExtn, tempDirectory);

        convertMainImage(originalFile, mainFile);
        maxImage = createImageFileDetail(sourceName, mainExtn, mainFile);
      }
  
      ImageFileDetail thumbImage = null;
      if (thumbWidth > 0 && thumbHeight > 0) {
        // convert the thumbnail image
        String thumbExtn = deriveThumbnailExtension(sourceExtension);
        String thumbExtra = "-thumb" + thumbWidth + "x" + thumbHeight + "-";
        File thumbFile = File.createTempFile(sourceName+thumbExtra, "."+thumbExtn, tempDirectory);

        convertThumbImage(originalFile, thumbFile);
        thumbImage = createImageFileDetail(sourceName, thumbExtn, thumbFile);
      }
  
      ImageFileDetail origImage = createImageFileDetail(sourceName, sourceExtension, originalFile);
      
      return new ImageFileSet(sourceName, sourceExtension, thumbImage, maxImage, origImage);
      
    } finally {
      originalStream.close();
    }
  }
  
  /**
   * Return the background colour used when ConvertMode.Pad is used.
   */
  public String getThumbNailBackground() {
    return thumbnailBackground;
  }

  /**
   * Set the background colour to be used with ConvertMode.Pad.
   */
  public void setThumbNailBackground(String thumbNailBackground) {
    this.thumbnailBackground = thumbNailBackground;
  }
  
  /**
   * Return the image format thumbnails are converted to.
   */
  public String getThumbnailExtension() {
    return thumbnailExtension;
  }

  /**
   * Set the image format (extension) that all thumbnails are converted to.
   * If this is null thumbnals are converted to jpeg or png (based on the source image).
   */
  public void setThumbnailExtension(String thumbnailExtension) {
    this.thumbnailExtension = thumbnailExtension;
  }

  protected String deriveThumbnailExtension(String extension) {
    if (thumbnailExtension != null) {
      return thumbnailExtension;
    }
    return deriveExtension(extension);
  }
  
  protected String deriveExtension(String sourceExtension) {
    
    String thumbExtn = sourceExtension;
    if (!thumbExtn.equalsIgnoreCase("png") && !thumbExtn.equalsIgnoreCase("jpg") && !thumbExtn.equalsIgnoreCase("jpeg")) {
      // default main image type
      thumbExtn = defaultMainImageExtension ;
    }
    return thumbExtn;
  }

  private void convertMainImage(File originalFile, File thumbFile) throws IOException {
    
    IMOperation op = new IMOperation();
    op.addImage(originalFile.getAbsolutePath());
    
    // resize if the image is bigger than the width or height, original size if smaller
    op.resize(width, height, ">");
    op.addImage(thumbFile.getAbsolutePath());
    try {
      ConvertCmd cmd = new ConvertCmd();
      cmd.run(op);
    } catch (Exception e) {
      throw new IOException("Error trying to generate thumbnail image", e);
    }
  }
  
  private void convertThumbImage(File originalFile, File thumbFile) throws IOException {
    
    IMOperation op = new IMOperation();
    op.addRawArgs("-define",deriveThumbDefine());
    op.addImage(originalFile.getAbsolutePath());
    op.addRawArgs("-auto-orient");
    
    if (thumbMode == ConvertMode.PadArea) {
      op.addRawArgs("-thumbnail",(thumbWidth*thumbHeight)+"@");
    } else {
      String option = (thumbMode == ConvertMode.Crop) ? "^": null;
      op.thumbnail(thumbWidth, thumbHeight, option);
    }
    
    // sharpen the image a little bit
    //op.addRawArgs("-unsharp","0x.5");
        
    if (thumbMode.hasBackground()) {
      op.addRawArgs("-background", thumbnailBackground);      
    }
    
    if (thumbMode.hasExtent()) {
      op.addRawArgs("-gravity", "center");
      op.addRawArgs("-extent", thumbWidth+"x"+thumbHeight);            
    }
    
    
    op.addImage(thumbFile.getAbsolutePath());    
    
    try {
      ConvertCmd cmd = new ConvertCmd();
      cmd.run(op);
    } catch (Exception e) {
      throw new IOException("Error trying to generate thumbnail image", e);
    }
  }
  
  /**
   * Set an initial size to 2 times the final thumb image width and height.
   */
  protected String deriveThumbDefine() {
    return "jpeg:size="+(thumbWidth*2)+"x"+(thumbHeight*2);
  }
  

  protected ImageFileDetail createImageFileDetail(String name, String extn, File file) throws IOException {
    
    BufferedImage in = ImageIO.read(file);
    int width = (in == null) ? 0 : in.getWidth();
    int height = (in == null) ? 0 : in.getHeight();
    return createImageFileDetail(name, extn, file, width, height);
  }
    
  protected ImageFileDetail createImageFileDetail(String name, String extn, File file, int width, int height) throws IOException {
    
    ImageFileDetail i = new ImageFileDetail();
    i.setName(name);
    i.setExtension(extn);
    i.setWidth(width);
    i.setHeight(height);
    i.setLength(file.length());
    i.setFile(file);
    return i;
  }
  
  public void pump(InputStream in, OutputStream out) throws IOException {
    
    if (in == null) throw new IOException("Input stream is null");
    if (out == null) throw new IOException("Output stream is null");

    try {
      try {
        byte[] buffer = new byte[4096];
        for (;;) {
          int bytes = in.read(buffer);
          if (bytes < 0) {
            break;
          }
          out.write(buffer, 0, bytes);
        }
      } finally {
        in.close();
      }
    } finally {
      out.close();
    }
  }
}

