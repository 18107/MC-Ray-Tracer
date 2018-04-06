package mod.id107.raytracer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

public class Shader {

	private int shaderProgram;
	private int vShader;
	private int fShader;
	private int vbo;
	private int worldChunkSsbo;
	private int chunkSsbo;
	private int worldMetadataSsbo;
	private int metadataSsbo;
	private int voxelDataSsbo;
	
	public void createShaderProgram() {
		shaderProgram = GL20.glCreateProgram();
		vShader = createShader(Reader.readShader("/mod/id107/raytracer/shaders/quad.vs"), GL20.GL_VERTEX_SHADER);
		fShader = createShader(Reader.readShader("/mod/id107/raytracer/shaders/frag.fs"), GL20.GL_FRAGMENT_SHADER);
		
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
		
		//load voxels
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16*16*16*4);
		byte[] data = Reader.readVoxel("/mod/id107/raytracer/voxel/error.mcvox");
		for (int i = 0; i < data.length; i++) {
			buffer.put(((int)data[i]&0xFF)/255f);
		}
		buffer.flip();
		
		worldChunkSsbo = GL15.glGenBuffers();
		chunkSsbo = GL15.glGenBuffers();
		worldMetadataSsbo = GL15.glGenBuffers();
		metadataSsbo = GL15.glGenBuffers();
		voxelDataSsbo = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, worldChunkSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, worldChunkSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, chunkSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, chunkSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, worldMetadataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 4, worldMetadataSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, metadataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 5, metadataSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, voxelDataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 6, voxelDataSsbo);
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
	
	public void deleteShaderProgram() {
		GL15.glDeleteBuffers(vbo);
		vbo = 0;
		GL15.glDeleteBuffers(worldChunkSsbo);
		worldChunkSsbo = 0;
		GL15.glDeleteBuffers(chunkSsbo);
		chunkSsbo = 0;
		GL15.glDeleteBuffers(worldMetadataSsbo);
		worldMetadataSsbo = 0;
		GL15.glDeleteBuffers(metadataSsbo);
		metadataSsbo = 0;
		GL15.glDeleteBuffers(voxelDataSsbo);
		voxelDataSsbo = 0;
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
	
	public int getWorldMetadataSsbo() {
		return worldMetadataSsbo;
	}
	
	public int getMetadataSsbo() {
		return metadataSsbo;
	}
	
	public int getVoxelDataSsbo() {
		return voxelDataSsbo;
	}
}
