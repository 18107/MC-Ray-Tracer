package mod.id107.raytracer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Reader {
	
	private static final byte[] header = {0,0,0,2, 0,0,0,16, 0,0,0,16, 0,0,0,16};

	public static String readShader(String resourceIn) {
		InputStream is = Reader.class.getResourceAsStream(resourceIn);
		if (is == null) {
			Log.info("Shader not found");
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		int i;
		
		try {
			i = is.read();
			while (i != -1) {
				sb.append((char) i);
				i = is.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
			
		}
		
		return sb.toString();
	}
	
	public static int[] readVoxel(String fileName) {
		InputStream is = Reader.class.getResourceAsStream(fileName);
		int[] data = new int[header.length];
		try {
			for (int i = 0; i < header.length; i++) {
				data[i] = is.read();
			}
			if (Arrays.equals(data, new int[] {0,0,0,2, 0,0,0,16, 0,0,0,16, 0,0,0,16})) {
				data = new int[16 + 16*16*16*4];
				for (int i = 0; i < data.length; i++) {
					data[i] = is.read();
				}
				is.close();
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int[] readTexture(String fileName) {
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		IResource resource = null;
		try {
			resource = resourceManager.getResource(new ResourceLocation(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		InputStream is = resource.getInputStream();
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int[] data = new int[width*height*4];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = bufferedImage.getRGB(x, y);
				data[y*width*4 + x*4] = (color>>16) & 0xFF;
				data[y*width*4 + x*4 + 1] = (color>>8) & 0xFF;
				data[y*width*4 + x*4 + 2] = color & 0xFF;
				data[y*width*4 + x*4 + 3] = (color>>24) & 0xFF;
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
