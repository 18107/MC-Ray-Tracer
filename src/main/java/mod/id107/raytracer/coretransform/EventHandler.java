package mod.id107.raytracer.coretransform;

import mod.id107.raytracer.RenderUtil;
import mod.id107.raytracer.world.WorldLoader;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load e) {
		if (RenderUtil.worldLoader != null) {
			RenderUtil.worldLoader.updateChunk(e.getChunk());
		}
	}
	
	/**
	 * Stops rendering while the player is changing dimension.
	 * @param e
	 */
	@SubscribeEvent
	public void playerStartChangeDimension(EntityTravelToDimensionEvent e) {
		RenderUtil.pauseRendering = true;
	}
	
	/**
	 * Reloads the world when the player changes dimension.
	 * @param e
	 */
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		CLTLog.info("World load " + e.getWorld().provider.getDimension());
		if (RenderUtil.worldLoader != null) {
			RenderUtil.pauseRendering = false;
			RenderUtil.worldLoader.setReloadWorld();
		}
	}
}
