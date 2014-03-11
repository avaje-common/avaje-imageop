package org.avaje.imageop.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import org.avaje.imageop.processor.ImageProcessor.Mode;
import org.junit.Assert;
import org.junit.Test;

public class ImageProcessorTest {

  @Test
  public void testWithThumb() throws IOException {
       
    Iterator<ImageReader> tiffReaders = ImageIO.getImageReadersByFormatName("TIFF");
    while (tiffReaders.hasNext()) {
        System.out.println("tiff reader: " + tiffReaders.next());
    }
    
    Iterator<ImageWriter> tiffWriters = ImageIO.getImageWritersByFormatName("TIFF");
    while (tiffWriters.hasNext()) {
        System.out.println("tiff writer: " + tiffWriters.next());
    }

    testResource(729, 88, "test-a.jpeg");  
    testResource(729, 88, "test-b.png");
    testResource(729, 88, "test-d.tiff");

    testResource(350, 40, "test-a.jpeg");  
    testResource(350, 40, "test-b.png");

    testResource(100, 80, 350, 40, "test-a.jpeg");
    testResource(100, 80, 350, 40, "test-b.png");
    testResource(100, 80, 350, 40, "test-d.tiff");

  }

  private void testResource( int width, int height, String resName) throws IOException {
    testResource( 0, 0, width, height, resName);
  }
  
  private void testResource( int thumbWidth, int thumbHeight, int width, int height, String resName) throws IOException {
      
    InputStream stream = getClass().getResourceAsStream("/"+resName);
    
    Assert.assertNotNull(stream);
    
    ImageProcessor processor = new  ImageProcessor(thumbWidth, thumbHeight, width, height, Mode.ScaleCrop, null);//new File("."));
    ImageFileSet imageSet = processor.process(stream, resName);
    
    Assert.assertNotNull(imageSet);
    ImageFileDetail normalImage = imageSet.getNormalImage();
    
    System.out.println("File Out: "+normalImage.getFile());
    
    Assert.assertNotNull(normalImage);
    Assert.assertEquals(height, normalImage.getHeight());
    Assert.assertEquals(width, normalImage.getWidth());

  }
  
}
