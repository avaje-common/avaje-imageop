package org.avaje.imageop.filter;

import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses the width and height as a maximum and retains the original images width
 * to height ratio.
 */
public class MaxSizeImageOp extends ImageOp {

  private static final Logger log = LoggerFactory.getLogger(MaxSizeImageOp.class);

  private final int maxWidth;
  private final int maxHeight;
  private final boolean withBorder;
  private final Paint borderPaint;

  /**
   * Construct so that images are scaled down so that they fit within a maxWidth
   * and maxHeight.
   * <p>
   * The resulting image maintains the original images aspect ratio.
   * </p>
   */
  public MaxSizeImageOp(int maxWidth, int maxHeight) {
    this(maxWidth, maxHeight, false, null);
  }

  /**
   * Construct with a border colour so that the images generated match the
   * maxWidth and maxHeight.
   * <p>
   * Images are scaled to fit inside the maxWidth and maxHeight and then a
   * border is added if necessary so that the image matches the maxWidth and
   * maxHeight.
   * </p>
   */
  public MaxSizeImageOp(int maxWidth, int maxHeight, boolean withBorder, Paint borderPaint) {
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    this.withBorder = withBorder;
    this.borderPaint = borderPaint;
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
    BufferedImage destImage = scaleFilter.filter(src, null);

    if (withBorder) {
      // Using the with border option so a horizontal or vertical
      // border will be added to 'pad' the image up so that it 
      // matches the maxWidth and maxHeight
      int vertical = 0;
      int horizontal = 0;
      if (height < maxHeight) {
        vertical = (maxHeight - height) / 2;
      }
      if (width < maxWidth) {
        horizontal = (maxWidth - width) / 2;
      }
      if (log.isDebugEnabled()) {
        log.debug("applying border vertical:{} horizontal:{}", vertical, horizontal);
      }

      BorderOp borderOp = new BorderOp(horizontal, vertical, horizontal, vertical, borderPaint, src.getType());
      destImage = borderOp.filter(destImage, null);
    }

    return destImage;
  }
}
