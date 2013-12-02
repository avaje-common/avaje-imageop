package org.avaje.imageop.processor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.avaje.imageop.filter.MaxSizeImageOp;
import org.junit.Assert;
import org.junit.Test;

public class MaxExpandFilterTest {

  @Test
  public void testSimple() throws IOException {
    
    //MaxSizeImageOp max = new MaxSizeImageOp(200, 150, true, new Color(255,255,255));
    MaxSizeImageOp max = new MaxSizeImageOp(200, 150, true, new Color(0,0,0));

    testMaxWithBorder(max, "test-a.jpeg","jpeg");    
    testMaxWithBorder(max, "test-c.jpeg","jpeg");     
  }

  private void testMaxWithBorder(MaxSizeImageOp max, String resName, String extn) throws IOException {
    InputStream inputStream = getInputStream(resName);
    BufferedImage in = ImageIO.read(inputStream);
    
    BufferedImage maxImage = max.filter(in, null);
    File thumbFile = File.createTempFile("test-"+resName, "."+extn);
    ImageIO.write(maxImage, extn, thumbFile);
  }
  
  private InputStream getInputStream(String resName) {
    InputStream stream = getClass().getResourceAsStream("/"+resName);
    Assert.assertNotNull(stream);
    return stream;
  }

}
