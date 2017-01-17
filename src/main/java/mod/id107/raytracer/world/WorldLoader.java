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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class WorldLoader {

	public static final int chunkSize = 16*16*16;
	
	private int[] worldChunks;
	private int[] worldMetadata;
	/**A list of free id's*/
	private Deque<Integer> chunkIdList; //TODO minimize size
	private Deque<Integer> metadataIdList;
	private int renderDistance;
	
	private Set<Chunk> chunksModified = new HashSet<Chunk>();
	
	private int playerChunkX;
	private int playerChunkY;
	private int playerChunkZ;
	
	private boolean updateAllChunks;
	private int timer;
	
	public WorldLoader() {
		updateAllChunks = true;
		timer = 60;
	}
	
	//TODO rename
	public void loadWorld(Minecraft mc, double posX, double posY, double posZ, Shader shader, float partialTicks) {
		int centerX = (int) Math.floor(posX/16);
		int centerZ = (int) Math.floor(posZ/16);
		int centerY = (int) Math.floor(posY/16);
		
		//TODO remove
		boolean showMetadata = false;
		if (showMetadata) {
			ExtendedBlockStorage storage = mc.theWorld.getChunkFromChunkCoords(centerX, centerZ).getBlockStorageArray()[centerY];
			if (storage != null) {
				int metadata = storage.get(7, 6, 11).getBlock().getMetaFromState(storage.get(7, 6, 11));
				Log.info(metadata);
			}
		}
		boolean showLightLevel = false;
		if (showLightLevel) {
			ExtendedBlockStorage storage = mc.theWorld.getChunkFromChunkCoords(centerX, centerZ).getBlockStorageArray()[centerY];
			if (storage != null) {
				int lightLevel = storage.getBlocklightArray().get(0, 0, 0);
				Log.info(lightLevel);
			}
		}
		
		if (timer != 0) {
			timer--;
		}
		
		//TODO upload chunks when available
		//if the render distance has changed
		if (renderDistance != mc.gameSettings.renderDistanceChunks || (updateAllChunks && timer == 0)) {
			renderDistance = mc.gameSettings.renderDistanceChunks;
			
			if (updateAllChunks == false) {
				updateAllChunks = true;
				timer = 60;
			} else if (timer == 0) {
				updateAllChunks = false;
			}
			
			playerChunkX = centerX;
			playerChunkZ = centerZ;
			
			//reinitialize the world
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
			
			//TODO minimize size
			//update the buffer size
			GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getChunkSsbo());
			GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, chunkSize*worldChunks.length*2*4, GL15.GL_DYNAMIC_DRAW);
			
			GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, shader.getMetadataSsbo());
			GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, chunkSize*worldMetadata.length*4, GL15.GL_DYNAMIC_DRAW);
			GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

			//inform the shader of the new render distance
			int renderDistanceUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "renderDistance");
			GL20.glUniform1i(renderDistanceUniform, renderDistance-1);
			
			//upload the chunks
			for (int z = 0; z < renderDistance*2-1; z++) {
				for (int x = 0; x < renderDistance*2-1; x++) {
					int chunkX = centerX+x-(renderDistance-1);
					int chunkZ = centerZ+z-(renderDistance-1);
					ExtendedBlockStorage[] storage = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ).getBlockStorageArray();
					for (int y = 0; y < 16; y++) {
						if (storage[y] != null) {
							int chunkId = chunkIdList.pop();
							worldChunks[z*(renderDistance*2-1)*16 + x*16 + y] = chunkId;
							loadChunk(chunkId, shader, storage[y]);
							
							int metadataId = metadataIdList.pop();
							boolean idUsed = loadMetadata(metadataId, shader, storage[y]);
							if (idUsed) {
								worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] = metadataId;
							} else {
								worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] = 0;
								metadataIdList.push(metadataId);
							}
						} else {
							worldChunks[z*(renderDistance*2-1)*16 + x*16 + y] = 0;
							worldMetadata[z*(renderDistance*2-1)*16 + x*16 + y] = 0;
						}
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
		
		//Update the players altitude
		if (playerChunkY != centerY) {
			playerChunkY = centerY;
			
			int chunkHeightUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "chunkHeight");
			GL20.glUniform1i(chunkHeightUniform, centerY);
		}
		
		//handle block updates
		Iterator<Chunk> iterator = chunksModified.iterator();
		while (iterator.hasNext()) {
			Chunk chunk = iterator.next();
			reloadChunk(chunk, shader);
		}
		chunksModified.clear();
		
		if (playerChunkX != centerX || playerChunkZ != centerZ) {
			//if the player has moved in the positive x direction
			if (centerX != playerChunkX) {
				moveX(mc, shader, centerX, centerZ, centerX > playerChunkX);
			}
			if (centerZ != playerChunkZ) {
				moveZ(mc, shader, centerX, centerZ, centerZ > playerChunkZ);
			}
			playerChunkX = centerX;
			playerChunkZ = centerZ;
			
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
	}
	
	//TODO lighting updates
	private void reloadChunk(Chunk chunk, Shader shader) {
		int chunkX = chunk.xPosition - playerChunkX + renderDistance-1;
		int chunkZ = chunk.zPosition - playerChunkZ + renderDistance-1;
		
		//bounds checking
		if (chunkX >= renderDistance*2-1 || chunkX < 0 ||
				chunkZ >= renderDistance*2-1 || chunkZ < 0) {
			return;
		}
		
		for (int y = 0; y < 16; y++) {
			ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y];
			int id = worldChunks[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y];
			if (id == 0) {
				if (storage == null) {
					continue;
				} else {
					id = chunkIdList.pop();
				}
			} else {
				if (storage == null) {
					worldChunks[chunkZ*(renderDistance*2-1)*16 + chunkX*16 + y] = 0;
					chunkIdList.push(id);
					continue;
				}
			}
			loadChunk(id, shader, storage);
		}
	}
	
	private void loadChunk(int id, Shader shader, ExtendedBlockStorage storage) {
		int[] data = new int[chunkSize*2];
		for (int y = 0; y < 16; y++) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
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
	
	private void moveX(Minecraft mc, Shader shader, int centerX, int centerZ, boolean positive) {
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
			ExtendedBlockStorage[] storage = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ).getBlockStorageArray();
			for (int y = 0; y < 16; y++) {
				if (storage[y] != null) {
					int chunkId = chunkIdList.pop();
					worldChunks[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = chunkId;
					loadChunk(chunkId, shader, storage[y]);
					int metadataId = metadataIdList.pop();
					boolean idUsed = loadMetadata(metadataId, shader, storage[y]);
					if (idUsed) {
						worldMetadata[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = metadataId;
					} else {
						worldMetadata[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = 0;
						metadataIdList.push(metadataId);
					}
				} else {
					worldChunks[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = 0;
					worldMetadata[z*(renderDistance*2-1)*16 + (positive ? renderDistance*2-2 : 0)*16 + y] = 0;
				}
			}
		}
	}
	
	private void moveZ(Minecraft mc, Shader shader, int centerX, int centerZ, boolean positive) {
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
			ExtendedBlockStorage[] storage = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ).getBlockStorageArray();
			for (int y = 0; y < 16; y++) {
				if (storage[y] != null) {
					int chunkId = chunkIdList.pop();
					worldChunks[(positive ? renderDistance*2-2 : 0)*(renderDistance*2-1)*16 + x*16 + y] = chunkId;
					loadChunk(chunkId, shader, storage[y]);
					int metadataId = metadataIdList.pop();
					boolean idUsed = loadMetadata(metadataId, shader, storage[y]);
					if (idUsed) {
						worldMetadata[(positive ? renderDistance*2-2 : 0)*(renderDistance*2-1)*16 + x*16 + y] = metadataId;
					} else {
						worldMetadata[(positive ? renderDistance*2-2 : 0)*(renderDistance*2-1)*16 + x*16 + y] = 0;
						metadataIdList.push(metadataId);
					}
				} else {
					worldChunks[(positive ? renderDistance*2-2 : 0)*(renderDistance*2-1)*16 + x*16 + y] = 0;
				}
			}
		}
	}
	
	public void chunkModified(Chunk chunk) {
		chunksModified.add(chunk);
	}
}
