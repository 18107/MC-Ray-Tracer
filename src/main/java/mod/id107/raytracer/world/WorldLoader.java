package mod.id107.raytracer.world;

import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import mod.id107.raytracer.Log;
import mod.id107.raytracer.RenderUtil;
import mod.id107.raytracer.Shader;
import mod.id107.raytracer.coretransform.CLTLog;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.registry.GameData;

public class WorldLoader {

	/**
	 * The number of blocks per 16×16×16 chunk.
	 */
	public static final int chunkSize = 16*16*16;
	
	public final Minecraft mc;
	
	public int dimension;
	
	/**
	 * An array containing the location of all chunks in VRAM.
	 */
	private int[] worldChunks;
	/**
	 * An array containing the location of all metadata in VRAM.
	 */
	private int[] worldMetadata;
	/**
	 * A list of free chunk id's
	 */
	private ArrayDeque<Integer> chunkIdList; //TODO minimize size?
	/**
	 * A list of free metadata id's
	 */
	private ArrayDeque<Integer> metadataIdList;
	
	/**
	 * Used to check if the render distance has changed.
	 */
	private int renderDistance;
	private boolean reloadWorld;
	
	/**
	 * TODO replace Chunk with ChunkCoord
	 */
	private ArrayDeque<Chunk> chunksModified = new ArrayDeque<Chunk>(24*24);
	
	private int playerChunkX;
	private int playerChunkY;
	private int playerChunkZ;
	
	/**
	 * Prevents the world updating multiple times while a player is teleporting.
	 */
	private boolean teleporting = false;
	
	public WorldLoader() {
		CLTLog.info("recreating");
		mc = Minecraft.getMinecraft();
		reloadWorld = true;
	}
	
	/**
	 * Gets a chunk from the chunk coordinates, then adds it
	 * to the queue if it exists and is not already in the queue.
	 * @param x chunk coordinate
	 * @param z chunk coordinate
	 * @return if the chunk exists
	 */
	public boolean updateChunk(int x, int z) {
		Chunk chunk = mc.world.getChunkFromChunkCoords(x, z);
		ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
		for (int y = 0; y < 16; y++) {
			if (storage[y] != null) {
				updateChunk(chunk);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds chunk to the end of the queue if it is not already in the queue.
	 * @param chunk
	 * @return if the chunk was added to the queue
	 */
	public boolean updateChunk(Chunk chunk) {
		synchronized (chunksModified) {
			if (!chunksModified.contains(chunk)) {
				chunksModified.add(chunk);
				return true;
			}
			return false;
		}
	}/**
	 * Gets a chunk from the chunk coordinates, then adds it
	 * to the beginning of the queue if it exists.
	 * @param x chunk coordinate
	 * @param z chunk coordinate
	 * @return if the chunk exists
	 */
	public boolean updateChunkFirst(int x, int z) {
		Chunk chunk = mc.world.getChunkFromChunkCoords(x, z);
		ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
		for (int y = 0; y < 16; y++) {
			if (storage[y] != null) {
				updateChunkFirst(chunk);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds chunk to the beginning of the queue.<br>
	 * If the chunk is already in the queue, the duplicate is removed first.
	 * @param chunk
	 */
	public void updateChunkFirst(Chunk chunk) {
		synchronized (chunksModified) {
			if (chunksModified.contains(chunk)) {
				chunksModified.remove(chunk);
			}
			chunksModified.push(chunk);
		}
	}
	
	public void setReloadWorld() {
		reloadWorld = true;
	}
	
	private void initializeWorld(int renderDistance, int centerX, int centerY, int centerZ, Shader shader) {
		
		playerChunkX = centerX;
		playerChunkZ = centerZ;
		
		//initialize the world
		worldChunks = new int[(renderDistance*2-1)*16*(renderDistance*2-1)];
		worldMetadata = new int[(renderDistance*2-1)*16*(renderDistance*2-1)];
		chunkIdList = new ArrayDeque<Integer>();
		metadataIdList = new ArrayDeque<Integer>();
		for (int i = worldChunks.length; i > 0; i--) {
			chunkIdList.push(i);
		}
		for (int i = worldMetadata.length; i > 0; i--) {
			metadataIdList.push(i);
		}
		
		//update the buffer size
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getChunkSsbo());
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, chunkSize*worldChunks.length*2*4, GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getMetadataSsbo());
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, chunkSize*worldMetadata.length*4, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		
		//inform the shader of the new render distance
		int renderDistanceUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "renderDistance");
		GL20.glUniform1i(renderDistanceUniform, renderDistance-1);
	}
	
	private void reloadWorld(int renderDistance, int centerX, int centerY, int centerZ, Shader shader) {
		initializeWorld(renderDistance, centerX, centerY, centerZ, shader);

		//upload the chunks in a spiral
		synchronized (chunksModified) {
			chunksModified.clear();
			updateChunk(centerX, centerZ);
			for (int r = 1; r <= renderDistance-1; r++) {
				for (int x = -r+1; x <= r; x++) {
					int z = -r;
					updateChunk(centerX+x, centerZ+z);
				}
				for (int z = -r+1; z <= r; z++) {
					int x = r;
					updateChunk(centerX+x, centerZ+z);
				}
				for (int x = r-1; x >= -r; x--) {
					int z = r;
					updateChunk(centerX+x, centerZ+z);
				}
				for (int z = r-1; z >= -r; z--) {
					int x = -r;
					updateChunk(centerX+x, centerZ+z);
				}
			}
		}

		//upload chunk coordinates
		IntBuffer worldChunkBuffer = BufferUtils.createIntBuffer(worldChunks.length);
		worldChunkBuffer.put(worldChunks);
		worldChunkBuffer.flip();
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getWorldChunkSsbo());
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, worldChunkBuffer, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

		//upload metadata coordinates
		IntBuffer worldMetadataBuffer = BufferUtils.createIntBuffer(worldMetadata.length);
		worldMetadataBuffer.put(worldMetadata);
		worldMetadataBuffer.flip();
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getWorldMetadataSsbo());
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, worldMetadataBuffer, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}
	
	public void updateWorld(double posX, double posY, double posZ, Shader shader) {
		
		int centerX = (int) Math.floor(posX/16);
		int centerZ = (int) Math.floor(posZ/16);
		int centerY = (int) Math.floor(posY/16);
		
		int renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
		//if the render distance has changed, or the player has moved too far
		if (this.renderDistance != renderDistance || reloadWorld) {
			reloadWorld = false;
			this.renderDistance = renderDistance;
			//wait until the center chunk exists before reloading the world
			if (!updateChunkFirst(centerX, centerZ)) {
				reloadWorld = true;
				return;
			}
			reloadWorld(renderDistance, centerX, centerY, centerZ, shader);
			CLTLog.info("reloading world #");
			return;
		}
		
		if (playerChunkX - centerX > renderDistance/2 ||
				playerChunkX - centerX < -renderDistance/2 ||
				playerChunkZ - centerZ > renderDistance/2 ||
				playerChunkZ - centerZ < -renderDistance/2) {
			teleporting = true;
		} else {
			if (teleporting) {
				teleporting = false;
				reloadWorld(renderDistance, centerX, centerY, centerZ, shader);
				return;
			}
		}
		
		//handle block updates
		synchronized (chunksModified) {
			while (!chunksModified.isEmpty()) {
				Chunk chunk = chunksModified.remove();
				int chunkX = chunk.xPosition - playerChunkX + renderDistance-1;
				int chunkZ = chunk.zPosition - playerChunkZ + renderDistance-1;
				int dimension = chunk.getWorld().provider.getDimension();
				
				if (chunkX < renderDistance*2-1 && chunkX >= 0 &&
						chunkZ < renderDistance*2-1 && chunkZ >= 0 &&
						dimension == this.dimension) {
					reloadChunk(chunk, chunkX, chunkZ, renderDistance, shader);
				}
			}
		}
		
		//Update the players altitude
		if (playerChunkY != centerY) {
			playerChunkY = centerY;

			int chunkHeightUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "chunkHeight");
			GL20.glUniform1i(chunkHeightUniform, centerY);
		}
		
		//Update the players chunk location
		if (playerChunkX != centerX || playerChunkZ != centerZ) {
			while (centerX != playerChunkX) {
				moveX(shader, centerX, centerZ, renderDistance, centerX > playerChunkX);
			}
			while (centerZ != playerChunkZ) {
				moveZ(shader, centerX, centerZ, renderDistance, centerZ > playerChunkZ);
			}
			playerChunkX = centerX;
			playerChunkZ = centerZ;
		}

		//upload chunk coordinates
		IntBuffer buffer = BufferUtils.createIntBuffer(worldChunks.length);
		buffer.put(worldChunks);
		buffer.flip();
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getWorldChunkSsbo());
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

		//upload metadata coordinates
		IntBuffer worldMetadataBuffer = BufferUtils.createIntBuffer(worldMetadata.length);
		worldMetadataBuffer.put(worldMetadata);
		worldMetadataBuffer.flip();
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getWorldMetadataSsbo());
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, worldMetadataBuffer, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}
	
	//TODO lighting updates
	private void reloadChunk(Chunk chunk, int chunkX, int chunkZ, int renderDistance, Shader shader) {
		
		for (int y = 0; y < 16; y++) {
			ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y];
			int id = worldChunks[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y];
			int metadataId = worldMetadata[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y];
			if (storage == null) {
				if (id == 0) {
					//do nothing
					continue;
				} else {
					//remove chunk
					worldChunks[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y] = 0;
					chunkIdList.push(id);
					if (metadataId != 0) {
						worldMetadata[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y] = 0;
						metadataIdList.push(metadataId);
					}
					continue;
				}
			} else {
				if (id == 0) {
					//create chunk
					id = chunkIdList.pop();
					worldChunks[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y] = id;
				}
				if (metadataId == 0) {
					metadataId = metadataIdList.pop();
					worldMetadata[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y] = metadataId;
				}
			}
			loadChunk(id, shader, storage);
			loadMetadata(metadataId, shader, storage);
		}
	}
	
	Set<IBlockState> stateSet = new HashSet<IBlockState>();
	
	//TODO handle lighting and metadata
	private void loadChunk(int id, Shader shader, ExtendedBlockStorage storage) {
		ObjectIntIdentityMap<IBlockState> map = GameData.getBlockStateIDMap();
		int[] data = new int[chunkSize*2];
		for (int y = 0; y < 16; y++) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					IBlockState state = storage.get(x, y, z);
					boolean fullBlock = state.isFullBlock();
					boolean cube = state.isFullCube();
					if (fullBlock != cube) {
						Log.info(state.getBlock().getUnlocalizedName() + ": " + fullBlock);
					}
					if (fullBlock) {
						stateSet.add(state);
					}
					int stateId = map.get(state); //TODO
					data[(y<<9) + (z<<5) + (x<<1)] = Block.getIdFromBlock(storage.get(x, y, z).getBlock());
				}
			}
		}
		
		for (int y = 0; y < 16; y++) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					data[(y<<9) + (z<<5) + (x<<1) + 1] = storage.getBlocklightArray().get(x, y, z);
				}
			}
		}
		
		IntBuffer buffer = BufferUtils.createIntBuffer(chunkSize*2);
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getChunkSsbo());
		GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, (id-1)*chunkSize*2*4, buffer);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}
	
	/**
	 * 
	 * @param id
	 * @param shader
	 * @param storage
	 * @return true if the id was used, false if there was nothing to upload
	 */
	private boolean loadMetadata(int id, Shader shader, ExtendedBlockStorage storage) {
		int[] data = new int[chunkSize];
		boolean containsValues = false;
		for (int y = 0; y < 16; y++) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					int metadata = storage.get(x, y, z).getBlock().getMetaFromState(storage.get(x, y, z));
					data[(y<<8) + (z<<4) + x] = metadata;
					if (metadata != 0) {
						containsValues = true;
					}
				}
			}
		}
		
		if (containsValues) {
			IntBuffer buffer = BufferUtils.createIntBuffer(chunkSize);
			buffer.put(data);
			buffer.flip();
			GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getMetadataSsbo());
			GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, (id-1)*chunkSize*4, buffer);
			GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		}
		
		return containsValues;
	}
	
	private void moveX(Shader shader, int centerX, int centerZ, int renderDistance, boolean positive) {
		//deallocate chunks
		for (int z = 0; z < renderDistance*2-1; z++) {
			for (int y = 0; y < 16; y++) {
				int chunkId = worldChunks[z*(renderDistance*2-1)*16 + (positive ? 0 : renderDistance*2-2)*16 + y];
				if (chunkId != 0) {
					chunkIdList.push(chunkId);
				}
				int metadataId = worldMetadata[z*(renderDistance*2-1)*16 + (positive ? 0 : renderDistance*2-2)*16 + y];
				if (metadataId != 0) {
					metadataIdList.push(metadataId);
				}
			}
		}
		
		//shift chunks
		if (positive) {
			for (int z = 0; z < renderDistance*2-1; z++) {
				for (int x = 0; x < renderDistance*2-2; x++) {
					for (int y = 0; y < 16; y++) {
						worldChunks[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldChunks[z*(renderDistance*2-1)*16 + (x+1)*16 + y];
						worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldMetadata[z*(renderDistance*2-1)*16 + (x+1)*16 + y];
					}
				}
			}
		} else {
			for (int z = 0; z < renderDistance*2-1; z++) {
				for (int x = renderDistance*2-2; x > 0; x--) {
					for (int y = 0; y < 16; y++) {
						worldChunks[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldChunks[z*(renderDistance*2-1)*16 + (x-1)*16 + y];
						worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldMetadata[z*(renderDistance*2-1)*16 + (x-1)*16 + y];
					}
				}
			}
		}
		
		//allocate new chunks
		int chunkX = centerX + (positive ? renderDistance-1 : -(renderDistance-1));
		for (int z = 0; z < renderDistance*2-1; z++) {
			int chunkZ = centerZ+z-(renderDistance-1);
			Chunk chunk = mc.world.getChunkFromChunkCoords(chunkX, chunkZ);
			ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
			for (int y = 0; y < 16; y++) {
				worldChunks[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = 0;
				worldMetadata[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = 0;
				if (storage[y] != null) {
					updateChunk(chunk);
				}
			}
		}
		
		playerChunkX += positive ? 1 : -1;
	}
	
	private void moveZ(Shader shader, int centerX, int centerZ, int renderDistance, boolean positive) {
		//deallocate chunks
		for (int x = 0; x < renderDistance*2-1; x++) {
			for (int y = 0; y < 16; y++) {
				int chunkId = worldChunks[(positive ? 0 : renderDistance*2-2)*(renderDistance*2-1)*16 + x*16 + y];
				if (chunkId != 0) {
					chunkIdList.push(chunkId);
				}
				int metadataId = worldMetadata[(positive ? 0 : renderDistance*2-2)*(renderDistance*2-1)*16 + x*16 + y];
				if (metadataId != 0) {
					metadataIdList.push(metadataId);
				}
			}
		}

		//shift chunks
		if (positive) {
			for (int z = 0; z < renderDistance*2-2; z++) {
				for (int x = 0; x < renderDistance*2-1; x++) {
					for (int y = 0; y < 16; y++) {
						worldChunks[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldChunks[(z+1)*(renderDistance*2-1)*16 + x*16 + y];
						worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldMetadata[(z+1)*(renderDistance*2-1)*16 + x*16 + y];
					}
				}
			}
		} else {
			for (int z = renderDistance*2-2; z > 0; z--) {
				for (int x = 0; x < renderDistance*2-1; x++) {
					for (int y = 0; y < 16; y++) {
						worldChunks[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldChunks[(z-1)*(renderDistance*2-1)*16 + x*16 + y];
						worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] =
								worldMetadata[(z-1)*(renderDistance*2-1)*16 + x*16 + y];
					}
				}
			}
		}

		//allocate new chunks
		int chunkZ = centerZ + (positive ? renderDistance-1 : -(renderDistance-1));
		for (int x = 0; x < renderDistance*2-1; x++) {
			int chunkX = centerX+x-(renderDistance-1);
			Chunk chunk = mc.world.getChunkFromChunkCoords(chunkX, chunkZ);
			ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
			for (int y = 0; y < 16; y++) {
				worldChunks[(positive ? renderDistance*2-2 : 0)*(renderDistance*2-1)*16 + x*16 + y] = 0;
				worldMetadata[(positive ? renderDistance*2-2 : 0)*(renderDistance*2-1)*16 + x*16 + y] = 0;
				if (storage[y] != null) {
					updateChunk(chunk);
				}
			}
		}
		
		playerChunkZ += positive ? 1 : -1;
	}
}
