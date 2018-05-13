package mod.id107.raytracer.chunk;

import java.util.ArrayList;
import java.util.HashMap;

public class MapUtil {

	private static int blockIndex = -1;
	private static HashMap<String, Integer> blocks = new HashMap<String, Integer>();
	
	private static int voxelIndex = -1;
	private static HashMap<String, Integer> voxels = new HashMap<String, Integer>();
	private static ArrayList<String> voxelKeys = new ArrayList<String>();
	
	private static int textureIndex = -1;
	private static HashMap<String, Integer> textures = new HashMap<String, Integer>();
	private static ArrayList<String> textureKeys = new ArrayList<String>();
	
	static {
		newBlock("air");
	}
	
	public static void reset() {
		blockIndex = -1;
		blocks = new HashMap<String, Integer>();
		voxelIndex = -1;
		voxels = new HashMap<String, Integer>();
		voxelKeys = new ArrayList<String>();
		textureIndex = -1;
		textures = new HashMap<String, Integer>();
		textureKeys = new ArrayList<String>();
		newBlock("air");
	}
	
	public static int newBlock(String name) {
		if (blocks.containsKey(name)) {
			throw new IllegalArgumentException("No duplicates are permitted");
		}
		blocks.put(name, ++blockIndex);
		return blockIndex;
	}
	
	public static int getBlock(String name) {
		Integer id = blocks.get(name);
		if (id == null) {
			throw new RuntimeException("block not found: " + name);
		} else {
			return id;
		}
	}
	
	private static int nextVoxel(String name) {
		Integer id = voxels.get(name);
		if (id == null) {
			voxels.put(name, ++voxelIndex);
			voxelKeys.add(name);
			return voxelIndex;
		} else {
			return id;
		}
	}
	
	public static int[][] nextVoxel(String name, int rotation) {
		int id = nextVoxel(name);
		return new int[][] {{1,id,rotation}, {1,id,rotation}, {1,id,rotation}, {1,id,rotation}, {1,id,rotation}, {1,id,rotation}};
	}
	
	public static int[][] nextVoxel(String[] name, int rotation) {
		if (name.length != 6) {
			throw new IllegalArgumentException("array must contain 6 elements");
		}
		int[] id = new int[name.length];
		for (int i = 0; i < id.length; i++) {
			id[i] = nextVoxel(name[i]);
		}
		return new int[][] {{1,id[0],rotation}, {1,id[1],rotation}, {1,id[2],rotation}, {1,id[3],rotation}, {1,id[4],rotation}, {1,id[5],rotation}};
	}
	
	public static int[][] nextVoxel(String name, int[] rotation) {
		if (rotation.length != 6) {
			throw new IllegalArgumentException("array must contain 6 elements");
		}
		int id = nextVoxel(name);
		return new int[][] {{1,id,rotation[0]}, {1,id,rotation[1]}, {1,id,rotation[2]}, {1,id,rotation[3]}, {1,id,rotation[4]}, {1,id,rotation[5]}};
	}
	
	public static int[][] nextVoxel(String[] name, int[] rotation) {
		if (name.length != 6 || rotation.length != 6) {
			throw new IllegalArgumentException("array must contain 6 elements");
		}
		int[] id = new int[name.length];
		for (int i = 0; i < id.length; i++) {
			id[i] = nextVoxel(name[i]);
		}
		return new int[][] {{1,id[0],rotation[0]}, {1,id[1],rotation[1]}, {1,id[1],rotation[2]}, {1,id[1],rotation[3]}, {1,id[1],rotation[4]}, {0,id[1],rotation[5]}};
	}
	
	private static int nextTexture(String name) {
		Integer id = textures.get(name);
		if (id == null) {
			textures.put(name, ++textureIndex);
			textureKeys.add(name);
			return textureIndex;
		} else {
			return id;
		}
	}
	
	public static int[][] nextTexture(String name, int rotation) {
		int id = nextTexture(name);
		return new int[][] {{0,id,rotation}, {0,id,rotation}, {0,id,rotation}, {0,id,rotation}, {0,id,rotation}, {0,id,rotation}};
	}
	
	public static int[][] nextTexture(String[] name, int rotation) {
		if (name.length != 6) {
			throw new IllegalArgumentException("array must contain 6 elements");
		}
		int[] id = new int[name.length];
		for (int i = 0; i < id.length; i++) {
			id[i] = nextTexture(name[i]);
		}
		return new int[][] {{0,id[0],rotation}, {0,id[1],rotation}, {0,id[2],rotation}, {0,id[3],rotation}, {0,id[4],rotation}, {0,id[5],rotation}};
	}
	
	public static int[][] nextTexture(String name, int[] rotation) {
		if (rotation.length != 6) {
			throw new IllegalArgumentException("array must contain 6 elements");
		}
		int id = nextTexture(name);
		return new int[][] {{0,id,rotation[0]}, {0,id,rotation[1]}, {0,id,rotation[2]}, {0,id,rotation[3]}, {0,id,rotation[4]}, {0,id,rotation[5]}};
	}
	
	public static int[][] nextTexture(String[] name, int[] rotation) {
		if (name.length != 6 || rotation.length != 6) {
			throw new IllegalArgumentException("array must contain 6 elements");
		}
		int[] id = new int[name.length];
		for (int i = 0; i < id.length; i++) {
			id[i] = nextTexture(name[i]);
		}
		return new int[][] {{0,id[0],rotation[0]}, {0,id[1],rotation[1]}, {0,id[1],rotation[2]}, {0,id[1],rotation[3]}, {0,id[1],rotation[4]}, {0,id[1],rotation[5]}};
	}
	
	public static String[] getAllBlocks() {
		return blocks.keySet().toArray(new String[0]);
	}
	
	public static String[] getAllVoxels() {
		return voxelKeys.toArray(new String[0]);
	}
	
	public static String[] getAllTextures() {
		return textureKeys.toArray(new String[0]);
	}
}
