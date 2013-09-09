package org.avaje.imageop.processor;

import java.io.IOException;
import java.io.InputStream;

import org.avaje.imageop.processor.ImageProcessor.Mode;
import org.junit.Assert;
import org.junit.Test;

public class TestProcessor {

  @Test
  public void testWithThumb() throws IOException {
       
    testResource(729, 88, "test-a.jpeg");  
    testResource(729, 88, "test-b.png");

    testResource(350, 40, "test-a.jpeg");  
    testResource(350, 40, "test-b.png");

    testResource(100, 80, 350, 40, "test-a.jpeg");
    testResource(100, 80, 350, 40, "test-b.png");

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
    
    Assert.assertNotNull(normalImage);
    Assert.assertEquals(height, normalImage.getHeight());
    Assert.assertEquals(width, normalImage.getWidth());

  }
  
}
