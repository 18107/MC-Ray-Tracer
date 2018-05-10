package mod.id107.raytracer.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class TraceableChunk {
	
	private static final int CHUNK_SIZE = 16;

	private int[][][][] blockData = new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE][3];
	
	public TraceableChunk(ExtendedBlockStorage blocks) {
		for (int z = 0; z < CHUNK_SIZE; z ++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int x = 0; x < CHUNK_SIZE; x++) {
					IBlockState blockState = blocks.get(x, y, z);
					int[] data = Maps.getBlock(Block.getStateId(blockState));
					blockData[z][y][x][0] = data[0];
					blockData[z][y][x][1] = data[1];
					blockData[z][y][x][2] = blockState.getLightValue(); //FIXME
				}
			}
		}
	}
	
	public int[] getBlockData(int x, int y, int z) {
		return blockData[z][y][x];
	}
	
	public int getBlockId(int x, int y, int z) {
		return blockData[z][y][x][0];
	}
	
	public int getBlockRotation(int x, int y, int z) {
		return blockData[z][y][x][1];
	}
	
	public int getBlockLightLevel(int x, int y, int z) {
		return blockData[z][y][x][2];
	}
}
