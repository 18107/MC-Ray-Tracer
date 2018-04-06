package mod.id107.raytracer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Reader {
	
	private static final byte[] header = {0,0,0,1, 0,0,0,16, 0,0,0,16, 0,0,0,16};

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
	
	public static byte[] readVoxel(String fileName) {
		InputStream is = Reader.class.getResourceAsStream(fileName);
		byte[] data = new byte[16];
		try {
			is.read(data);
			if (Arrays.equals(data, header)) {
				data = new byte[16*16*16*4];
				is.read(data);
				is.close();
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
