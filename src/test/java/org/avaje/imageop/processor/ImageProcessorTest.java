package org.avaje.imageop.processor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class ImageProcessorTest {

  @Test
  public void testWithThumb() throws IOException {

    
    testResource(ConvertMode.PadArea, 150, 150, 600, 600, "ethan.jpg", 450);
//    testResource(ConvertMode.Pad, 200, 200, 600, 600, "original-1094.tif", 600);
   
//    testResource(ConvertMode.PadArea, 100, 100, 200, 100, "test-x.jpg", 129);
    //testResource(ConvertMode.Pad, 100, 100, 200, 100, "test-x.jpg", 129);
    //testResource(ConvertMode.Crop, 100, 100, 200, 100, "test-x.jpg", 129);
    //testResource(ConvertMode.Max, 100, 100, 200, 100, "test-x.jpg", 129);
       
    //testResource(Mode.Pad, 100, 80, 350, 40, "test-a.jpeg", 336);

//    Mode mode = Mode.Max;
//
//    testResource(mode, 729, 88, "test-a.jpeg");  
//    testResource(mode, 729, 88, "test-b.png");
//    testResource(mode, 729, 88, "test-d.tiff", 127);
//    testResource(mode, 350, 40, "test-a.jpeg", 336);  
//    testResource(mode, 350, 40, "test-b.png", 331);
//
//    testResource(mode, 100, 80, 350, 40, "test-a.jpeg", 336);
//    testResource(mode, 100, 80, 350, 40, "test-b.png", 331);
//    testResource(mode, 100, 80, 350, 40, "test-d.tiff", 58);
//    testResource(mode, 100, 80, 350, 40, "test-e.tiff", 26);
    
  }
  private void testResource(ConvertMode mode, int width, int height, String resName) throws IOException {
    testResource(mode, width, height, resName, 0);
  }
  
  private void testResource(ConvertMode mode, int width, int height, String resName, int assertWidth) throws IOException {
    testResource(mode, 0, 0, width, height, resName, assertWidth);
  }

  private void testResource(ConvertMode mode, int thumbWidth, int thumbHeight, int width, int height, String resName) throws IOException {
    testResource(mode, thumbWidth, thumbHeight, width, height, resName, 0);
  }
    
  private void testResource(ConvertMode mode, int thumbWidth, int thumbHeight, int width, int height, String resName, int assertWidth) throws IOException {
      
    URL resource = getClass().getResource("/"+resName);
    String fileName = resource.getFile();
    
    File file = new File(fileName);
    
    Assert.assertNotNull(file);
    Assert.assertTrue(file.exists());
    
    ImageProcessor processor = new  ImageProcessor(thumbWidth, thumbHeight, width, height, mode, null);//new File("."));
    processor.setThumbnailExtension("jpg");
    ImageFileSet imageSet = processor.process(file, resName);
    
    Assert.assertNotNull(imageSet);
    ImageFileDetail normalImage = imageSet.getNormalImage();
    
    System.out.println("File Out: "+normalImage.getFile());
    
    Assert.assertNotNull(normalImage);
    Assert.assertTrue(""+normalImage.getHeight()+" and "+height, height <= (normalImage.getHeight()+1));
    Assert.assertTrue(""+normalImage.getHeight(), height >= (normalImage.getHeight()-1));
    if (assertWidth > 0) {
      Assert.assertEquals(assertWidth, normalImage.getWidth());      
    } else {
      Assert.assertEquals(width, normalImage.getWidth());
    }
  }
  
}
