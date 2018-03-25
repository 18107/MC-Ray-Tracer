package core.id107.raytracer.coretransform;

import core.id107.raytracer.RayTracerEvent;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

public class TransformerUtil {

	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.client.Minecraft#loadWorld() loadWorld}
	 */
	public static void onWorldLoad(WorldClient worldClient) {
		if (worldClient != null) {
			MinecraftForge.EVENT_BUS.post(new RayTracerEvent.WorldLoadEvent());
		} else {
			MinecraftForge.EVENT_BUS.post(new RayTracerEvent.WorldUnloadEvent());
		}
	}
	
	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.world.chunk.Chunk#setBlockState() setBlockState}
	 */
	public static void onChunkModified(Chunk chunk) {
		MinecraftForge.EVENT_BUS.post(new RayTracerEvent.ChunkModifiedEvent(chunk));
	}
}
