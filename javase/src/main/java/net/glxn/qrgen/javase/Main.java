package net.glxn.qrgen.javase;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import net.glxn.qrgen.core.image.ImageType;

public class Main {
	public static void main(String[] args) throws Exception {
		String s = "0123456789";
		for (int i = 0; i < 9; i++) {
			s += s;
		}
		System.out.println(s.length());
		
		final AtomicReference<BufferedImage> r = new AtomicReference<>();
		
		Frame frame = new Frame(){
			@Override
			public void paint(Graphics g) {
				BufferedImage img = r.get();
				if (img != null){					
					g.drawImage(img, 0, 0, null);
				}
			}
		};
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		r.set(toQR("Hi"));
		frame.repaint();
	}
	
	private static BufferedImage toQR(String s) throws Exception {
		byte[] b = QRCode.from(s).to(ImageType.JPG).withSize(500, 500).stream().toByteArray();
		return ImageIO.read(new ByteArrayInputStream(b));
		
	}
	
	private static void fromQR() throws Exception {
		LuminanceSource tmpSource = new BufferedImageLuminanceSource(ImageIO.read(new File("H.jpg")));
	    BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
	    Result result = new com.google.zxing.qrcode.QRCodeReader().decode(tmpBitmap);
	    System.out.println("R");
	    System.out.println(result.getText());
	}
}
