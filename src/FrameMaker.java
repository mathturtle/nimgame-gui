import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import waldonsm.nimgame.gui.animation.Explosion;


public class FrameMaker {

	public static void main(String[] args) {
		try {
		File file = new File("/Users/shawn/Desktop/frames.zip");
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
		Explosion ex = new Explosion(4, new Runnable() {public void run(){}});
		int i = 0;
		while (!ex.isDone()) {
			BufferedImage image = new BufferedImage(100,100,BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = image.createGraphics();
			ex.draw(g);
			zos.putNextEntry(new ZipEntry("frame" + i + ".png"));
			if (!ImageIO.write(image, "png", zos)) {
				System.out.println("uh-oh");
			}
			zos.closeEntry();
			ex.next();
			i++;
		}
		zos.close();
		
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
