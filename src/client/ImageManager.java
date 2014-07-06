package client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ImageManager {
	private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	public static BufferedImage getImage(String path) {
		if (images.containsKey(path)) {
			return images.get(path);
		} else {
			BufferedImage image = null;
			try {
				InputStream stream = ImageManager.class.getResourceAsStream(path);
				if (stream == null) {
					System.err.println("Failed to load image: " + path);
					image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				} else {
					image = ImageIO.read(stream);
				}
			} catch (IOException e) {
				System.err.println("Failed to load image: " + path);
				image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			}
			images.put(path, image);
			return image;
		}
	}
}
