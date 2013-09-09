package org.avaje.imageop.filter;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses the width and height as a maximum and retains the original images width to height ratio.
 */
public class MaxSizeImageOp extends ImageOp {

  private static final Logger log = LoggerFactory.getLogger(MaxSizeImageOp.class);
  
  private final int maxWidth;
  private final int maxHeight;

  public MaxSizeImageOp(int maxWidth, int maxHeight) {
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
  }

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    int origHeight = src.getHeight();
    int origWidth = src.getWidth();

    if (origHeight <= maxHeight && origWidth <= maxWidth) {
      log.debug("No scaling required for MaxSize filter, width and height fine.");
      return src;
    }

    BigDecimal maxWidthBd = new BigDecimal(maxWidth);
    BigDecimal maxHeightBd = new BigDecimal(maxHeight);

    BigDecimal origHeightAsBd = new BigDecimal(origHeight);
    BigDecimal origWidthAsBd = new BigDecimal(origWidth);

    BigDecimal scaleWidth = origWidthAsBd.divide(maxWidthBd, 6, RoundingMode.HALF_DOWN);
    BigDecimal scaleHeight = origHeightAsBd.divide(maxHeightBd, 6, RoundingMode.HALF_DOWN);
    BigDecimal maxScale = scaleWidth.max(scaleHeight);

    if (log.isDebugEnabled()) {
      log.debug("scaleWidth:{} scaleHeight:{}", scaleWidth.doubleValue(), scaleHeight.doubleValue());
    }
    
    int width = maxWidth;
    int height = maxHeight;

    height = origHeightAsBd.divide(maxScale, RoundingMode.HALF_DOWN).intValue();
    width = origWidthAsBd.divide(maxScale, RoundingMode.HALF_DOWN).intValue();

    if (log.isDebugEnabled()) {
      log.debug("width:{} height:{}", width, height);
    }

    ScaleImageOp scaleFilter = new ScaleImageOp(width, height);
    return scaleFilter.filter(src, null);
  }

}
