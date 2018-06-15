package mod.id107.raytracer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import mod.id107.raytracer.chunk.Maps;

public class Shader {

	private int shaderProgram;
	private int vShader;
	private int fShader;
	private int vbo;
	private int worldChunkSsbo;
	private int chunkSsbo;
	private int idMapSsbo;
	private int voxelDataSsbo;
	private int textureDataSsbo;
	
	public void createShaderProgram() {
		shaderProgram = GL20.glCreateProgram();
		vShader = createShader(Reader.readShader("raytracer:shaders/quad.vs"), GL20.GL_VERTEX_SHADER);
		fShader = createShader(Reader.readShader("raytracer:shaders/frag.fs"), GL20.GL_FRAGMENT_SHADER);
		
		GL20.glAttachShader(shaderProgram, vShader);
		GL20.glAttachShader(shaderProgram, fShader);
		GL20.glBindAttribLocation(shaderProgram, 0, "vertex");
		GL30.glBindFragDataLocation(shaderProgram, 0, "color");
		GL20.glLinkProgram(shaderProgram);
		
		GL20.glDetachShader(shaderProgram, vShader);
		GL20.glDetachShader(shaderProgram, fShader);
		GL20.glDeleteShader(vShader);
		vShader = 0;
		GL20.glDeleteShader(fShader);
		fShader = 0;
		
		int linked = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
		String programLog = GL20.glGetProgramInfoLog(shaderProgram, GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH));
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		
		//init vbo
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		ByteBuffer bb = BufferUtils.createByteBuffer(2 * 6);
		bb.put((byte) -1).put((byte) -1);
		bb.put((byte) 1).put((byte) -1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) -1).put((byte) 1);
		bb.put((byte) -1).put((byte) -1);
		bb.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bb, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		Maps.loadMaps();
		IntBuffer idMapBuffer = loadIdMap();
		
		//load voxels TODO move this to a better location
		FloatBuffer voxelBuffer = loadVoxels();
		
		//load textures
		FloatBuffer textureBuffer = loadTextures();
		
		worldChunkSsbo = GL15.glGenBuffers();
		chunkSsbo = GL15.glGenBuffers();
		idMapSsbo = GL15.glGenBuffers();
		voxelDataSsbo = GL15.glGenBuffers();
		textureDataSsbo = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, worldChunkSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, worldChunkSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, chunkSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, chunkSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, idMapSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, idMapBuffer, GL15.GL_STATIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 4, idMapSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, voxelDataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, voxelBuffer, GL15.GL_STATIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 5, voxelDataSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, textureDataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, textureBuffer, GL15.GL_STATIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 6, textureDataSsbo);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}
	
	private int createShader(String resource, int type) {
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, resource);
		GL20.glCompileShader(shader);
		int compiled = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
		String shaderLog = GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}
	
	/**Loads all of the voxel grids into a {@link FloatBuffer}
	 * @return the flipped buffer*/
	private FloatBuffer loadVoxels() {
		String[] voxels = Maps.getAllVoxels();
		FloatBuffer voxelBuffer = BufferUtils.createFloatBuffer(16*16*16*4*voxels.length);
		int[] data;
		try {
			for (String voxel : voxels) {
				data = Reader.readQubicle("raytracer:voxel/" + voxel + ".qb"); //TODO
				for (int i = 0; i < data.length; i++) {
					voxelBuffer.put(data[i]/255f);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		voxelBuffer.flip();
		return voxelBuffer;
	}
	
	/**Loads all of the textures into a {@link FloatBuffer}
	 * @return the flipped buffer*/
	private FloatBuffer loadTextures() {
		String[] textures = Maps.getAllTextures();
		FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(16*16*4*textures.length);
		int[] data;
		for (String texture : textures) {
			data = Reader.readTexture("textures/blocks/" + texture + ".png"); //TODO
			for (int i = 0; i < data.length; i++) {
				textureBuffer.put(data[i]/255f);
			}
		}
		textureBuffer.flip();
		return textureBuffer;
	}
	
	private IntBuffer loadIdMap() {
		int[][][] map = Maps.getIdMap();
		IntBuffer buffer = BufferUtils.createIntBuffer(map.length*6*3);
		for (int[][] side : map) {
			for (int[] data : side) {
				for (int value : data) {
					buffer.put(value);
				}
			}
		}
		buffer.flip();
		return buffer;
	}
	
	public void deleteShaderProgram() {
		GL15.glDeleteBuffers(vbo);
		vbo = 0;
		GL15.glDeleteBuffers(worldChunkSsbo);
		worldChunkSsbo = 0;
		GL15.glDeleteBuffers(chunkSsbo);
		chunkSsbo = 0;
		GL15.glDeleteBuffers(idMapSsbo);
		idMapSsbo = 0;
		GL15.glDeleteBuffers(voxelDataSsbo);
		voxelDataSsbo = 0;
		GL15.glDeleteBuffers(textureDataSsbo);
		textureDataSsbo = 0;
		GL20.glDeleteProgram(shaderProgram);
		shaderProgram = 0;
	}
	
	public int getShaderProgram() {
		return shaderProgram;
	}
	
	public int getVbo() {
		return vbo;
	}
	
	public int getWorldChunkSsbo() {
		return worldChunkSsbo;
	}
	
	public int getChunkSsbo() {
		return chunkSsbo;
	}
	
	public int getIdMapSsbo() {
		return idMapSsbo;
	}
	
	public int getVoxelDataSsbo() {
		return voxelDataSsbo;
	}
	
	public int getTextureDataSsbo() {
		return textureDataSsbo;
	}
}
