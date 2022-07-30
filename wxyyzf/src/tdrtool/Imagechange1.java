package tdrtool;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Imagechange1 {
	/*
	 * 根据尺寸图片居中裁剪
	 */
	public void cutCenterImage(String src, String dest, int w, int h)
			throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		Rectangle rect = new Rectangle((reader.getWidth(imageIndex) - w) / 2,
				(reader.getHeight(imageIndex) - h) / 2, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "jpg", new File(dest));

	}

	/*
	 * 图片裁剪二分之一
	 */
	public void cutHalfImage(String src, String dest) throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		int width = reader.getWidth(imageIndex) / 2;
		int height = reader.getHeight(imageIndex) / 2;
		Rectangle rect = new Rectangle(width / 2, height / 2, width, height);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "jpg", new File(dest));
	}

	/*
	 * 图片裁剪通用接口
	 */

	public void cutImage(String src, String dest, int x, int y, int w, int h)
			throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "jpg", new File(dest));

	}

	/*
	 * 图片缩放-绝对缩放
	 */
	public void zoomImage(String src, String dest, int w, int h)
			throws Exception {
		double wr = 0, hr = 0;
		File srcFile = new File(src);
		File destFile = new File(dest);
		BufferedImage bufImg = ImageIO.read(srcFile);
		Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);
		wr = w / bufImg.getWidth();
		hr = h / bufImg.getHeight();
		AffineTransformOp ato = new AffineTransformOp(
				AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		try {
			ImageIO.write((BufferedImage) Itemp,
					dest.substring(dest.lastIndexOf(".") + 1), destFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/*
	 * 图片缩放-保持纵横比缩放
	 */
	public void zoomImagewithwh(String src, String dest, int iw, int ih)
			throws Exception {
		double wr = 0, hr = 0;
		int fw = 0, fh = 0;// 计算出的最终高宽
		double pw = 0, ph = 0;// 图片实际高宽
		File srcFile = new File(src);
		File destFile = new File(dest);
		BufferedImage bufImg = ImageIO.read(srcFile);
		pw = bufImg.getWidth();
		ph = bufImg.getHeight();
		if ((pw > iw) || (ph > ih)) {
			double deg = 1.000;
			while ((deg > 0) & ((pw * deg >iw) || (ph * deg > ih))) {
				deg = deg - 0.001;
			}
            fw=(int)Math.round(pw*deg);
            fh=(int)Math.round(ph*deg);
			Image Itemp = bufImg.getScaledInstance(fw, fh, bufImg.SCALE_SMOOTH);
			wr = fw * 1.0 / bufImg.getWidth();
			hr = fh * 1.0 / bufImg.getHeight();
			AffineTransformOp ato = new AffineTransformOp(
					AffineTransform.getScaleInstance(wr, hr), null);
			Itemp = ato.filter(bufImg, null);
			try {
				ImageIO.write((BufferedImage) Itemp,
						dest.substring(dest.lastIndexOf(".") + 1), destFile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}else{
			if(srcFile.exists()){srcFile.renameTo(new File(dest));}
		}

	}
}