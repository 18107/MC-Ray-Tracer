package mod.id107.raytracer.coretransform;

import java.util.HashMap;
import java.util.Map;

import mod.id107.raytracer.RenderUtil;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	
	@SubscribeEvent
	public void onChunkLoad(Load e) {
		//CLTLog.info("onChunkLoad   " + e.getChunk().xPosition + " " + e.getChunk().zPosition);
		if (RenderUtil.worldLoader != null) {
			RenderUtil.worldLoader.updateChunk(e.getChunk()); //TODO multiple dimensions
		}
	}
}
