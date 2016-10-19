package net.glxn.qrgen.javase;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import net.glxn.qrgen.core.image.ImageType;

public class Main {
	public static void main(String[] args) throws Exception {

//		toScreen(null);
//		Thread.sleep(300000);
//		File file = new File("C:\\devsrc\\digzone\\bats\\kelvin\\Albert.zip");
		receive(new File("Albert.zip"));
	}
	
	public static void receive(File file) throws Exception {
		FileOutputStream out = new FileOutputStream(file);
		int fragment = 0;
		while(true){
			String s;
			try {
				s = fromQR(fromScreen());
			} catch (Exception e) {
				continue;
			}
			byte[] bb = DatatypeConverter.parseBase64Binary(s.substring(0, 8));
			int actualFragment = from8(bb);
			System.out.print(actualFragment);
			if (fragment == actualFragment){
				out.write(DatatypeConverter.parseBase64Binary(s.substring(8,s.length())));
				out.flush();
				System.out.println();
				System.out.print(fragment);
				fragment++;
			} else if (fragment-1 == actualFragment) {
//				new Robot().keyPress(KeyEvent.VK_CONTROL);
//				new Robot().keyRelease(KeyEvent.VK_CONTROL);
			} else {
				new Robot().mouseMove(
						(int)(MouseInfo.getPointerInfo().getLocation().x+(Math.random()>0.5?1:-1)),
						(int)(MouseInfo.getPointerInfo().getLocation().y+(Math.random()>0.5?1:-1))
						);
			}
		}
		
	}
	
	public static void send(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        int fragment =0;
        int readbyte;
        byte[] bytes = new byte[1024];
        while ((readbyte=in.read(bytes))>0) {
            if (readbyte != bytes.length) {
                bytes = Arrays.copyOf(bytes, readbyte);
                System.out.println(readbyte);
            } else {
                System.out.print("X");
            }
            toScreen(toQR(DatatypeConverter.printBase64Binary(to8(fragment++)) + DatatypeConverter.printBase64Binary(bytes)));
            COOL.set(false);
            int waited=0;
            while (!COOL.get()) {            	
            	Thread.sleep(1000);
            	if (waited++>20) {
            		break;
            	}
            }
        }
        System.out.println("DONE");
        in.close();
    }
	
	private static BufferedImage fromScreen() throws Exception {
		return new Robot().createScreenCapture(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
	}
	
	private static Frame frame;
	private static final AtomicReference<BufferedImage> r = new AtomicReference<>();
	private static final AtomicBoolean COOL = new AtomicBoolean();
	
	private static void toScreen(BufferedImage img) throws Exception {
		
		if (frame == null) {			
			frame = new Frame(){
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
			Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
				
				@Override
				public void eventDispatched(AWTEvent event) {
					BufferedImage o = r.get();
					if (o != null) {
						COOL.set(true);				
					}
				}
			}, AWTEvent.KEY_EVENT_MASK);
		}
		
		r.set(img);
		frame.repaint();
	}
	
	private static BufferedImage toQR(String s) throws Exception {
		try {
			byte[] b = QRCode.from(s).to(ImageType.JPG).withSize(500, 500).stream().toByteArray();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
			String actual = fromQR(img);
			if (!s.replaceFirst("@+", "").equals(actual.replaceFirst("@+", ""))){
				throw new RuntimeException("toQR mismatch");
			}
			return img;
		} catch (Exception e) {
			if (!s.startsWith("@@@@@@@@@@")) {
				return toQR("@"+s);
			}
			throw new RuntimeException("Fail toQR", e);
		}
		
	}
	
	private static String fromQR(BufferedImage img) throws Exception {
        try {
            LuminanceSource tmpSource = new BufferedImageLuminanceSource(img);
            BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
            Result result = new com.google.zxing.qrcode.QRCodeReader().decode(tmpBitmap);
            return result.getText().replaceFirst("@+", "");
        } catch(Exception e) {
            throw new RuntimeException("Fail fromQR", e);
        }
	}
	
	private static int from8(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}
	private static byte[] to8(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}
}
