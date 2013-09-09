package org.avaje.imageop.filter;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CropScaleImageOp extends ImageOp {

  private static final Logger log = LoggerFactory.getLogger(CropScaleImageOp.class);
  
  private final int maxWidth;
  private final int maxHeight;
  private final BigDecimal maxWidthBd;
  private final BigDecimal maxHeightBd;

  private final ScaleImageOp scaleFilter;
  
  public CropScaleImageOp(int maxWidth, int maxHeight) {
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    this.maxWidthBd = new BigDecimal(maxWidth);
    this.maxHeightBd = new BigDecimal(maxHeight);
    this.scaleFilter = new ScaleImageOp(maxWidth, maxHeight);
  }

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    int origHeight = src.getHeight();
    int origWidth = src.getWidth();

    if (maxHeight == origHeight && maxWidth == origWidth) {
      log.debug("No crop or scale required");
      return src;
    }
    
    BigDecimal origHeightAsBd = new BigDecimal(origHeight);
    BigDecimal origWidthAsBd = new BigDecimal(origWidth);

    BigDecimal scaleWidth = origWidthAsBd.divide(maxWidthBd, 6, RoundingMode.HALF_DOWN);
    BigDecimal scaleHeight = origHeightAsBd.divide(maxHeightBd, 6, RoundingMode.HALF_DOWN);

    int minsWint = origWidth;
    int minsHint = origHeight;

    log.debug("o:{}x{} max:{}x{} scaleWidth:{} scaleHeight:{}" ,origWidth, origHeight, maxWidth, maxHeight, scaleWidth, scaleHeight);

    int x = 0;
    int y = 0;
    BigDecimal minScaleDb = scaleWidth.min(scaleHeight);
    BigDecimal maxScaleDb = scaleWidth.max(scaleHeight);
    if (minScaleDb.compareTo(scaleWidth) == 0) {
      // width was min
      BigDecimal tmp = origHeightAsBd.multiply(minScaleDb);
      minsHint = tmp.divide(maxScaleDb, 6, RoundingMode.HALF_DOWN).intValue();
      log.debug("change height minsHint:{}", minsHint);
      y = (origHeight - minsHint) / 2;
      
    } else {
      BigDecimal tmp = origWidthAsBd.multiply(minScaleDb);
      minsWint = tmp.divide(maxScaleDb, 6, RoundingMode.HALF_DOWN).intValue();
      log.debug("change width minsWint:{}", minsWint);
      x = (origWidth - minsWint) / 2;
    }

    // Crop the image first
    CropImageOp cropFilter = new CropImageOp(x, y, minsWint, minsHint);
    BufferedImage croppedImg = cropFilter.filter(src, null);

    // Now scale the image
    return scaleFilter.filter(croppedImg, null);
  }

}
