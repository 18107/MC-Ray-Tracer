package mod.id107.raytracer.coretransform;

import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

	@SubscribeEvent
	public void onChunkLoad(Load e) {
		//CLTLog.info("onChunkLoad   " + e.getChunk().xPosition + " " + e.getChunk().zPosition);
	}
	
	@SubscribeEvent
	public void onChunkUnload(Unload e) {
		//CLTLog.info("onChunkUnload " + e.getChunk().xPosition + " " + e.getChunk().zPosition);
	}
}
