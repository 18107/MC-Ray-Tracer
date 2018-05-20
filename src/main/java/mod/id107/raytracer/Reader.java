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
	
	public static int[] readQubicle(String fileName) throws IOException {
		InputStream is = Reader.class.getResourceAsStream(fileName);
		if (is == null) {
			throw new IOException("File not found: " + fileName);
		}
		byte[] data = new byte[4];
		is.read(data);
		if (!Arrays.equals(data, new byte[] {1,1,0,0})) {
			throw new IOException(String.format("%s Got version %d.%d.%d.%d, supported version is %d.%d.%d.%d",
					fileName, data[0], data[1], data[2], data[3], 1, 1, 0, 0));
		}
		is.read(data);
		if (!Arrays.equals(data, new byte[] {0,0,0,0})) {
			throw new IOException(String.format("%s Color must be RGBA, not BGRA", fileName));
		}
		is.read(data);
		if (!Arrays.equals(data, new byte[] {0,0,0,0})) {
			throw new IOException(String.format("%s Axis must be left handed", fileName));
		}
		is.read(data);
		if (!Arrays.equals(data, new byte[] {0,0,0,0})) {
			throw new IOException(String.format("%s Data must not be compressed", fileName));
		}
		is.read(data); //visibility mask encoded
		is.read(data);
		int matrixCount = (data[0]&0xFF) | ((data[1]&0xFF)<<8) | ((data[2]&0xFF)<<16) | ((data[3]&0xFF)<<24);
		if (matrixCount != 1) {
			Log.info("Support for multiple matricies has not been implemented yet");
			Log.info("Using first matrix only for " + fileName);
		}
		int nameLength = is.read();
		byte[] name = new byte[nameLength];
		is.read(name);
		is.read(data);
		int xSize = (data[0]&0xFF) | ((data[1]&0xFF)<<8) | ((data[2]&0xFF)<<16) | ((data[3]&0xFF)<<24);
		is.read(data);
		int ySize = (data[0]&0xFF) | ((data[1]&0xFF)<<8) | ((data[2]&0xFF)<<16) | ((data[3]&0xFF)<<24);
		is.read(data);
		int zSize = (data[0]&0xFF) | ((data[1]&0xFF)<<8) | ((data[2]&0xFF)<<16) | ((data[3]&0xFF)<<24);
		if (xSize != 16 || ySize != 16 || zSize != 16) {
			throw new IOException(String.format("%s Voxel grid is %dx%dx%d. It must be 16x16x16",
					fileName, xSize, ySize, zSize));
		}
		is.read(data); //xPos
		is.read(data); //yPos
		is.read(data); //zPos
		int[] matrix = new int[xSize*ySize*zSize*4];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = is.read();
		}
		is.close();
		return matrix;
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
