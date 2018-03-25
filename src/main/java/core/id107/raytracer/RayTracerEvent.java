package core.id107.raytracer;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RayTracerEvent extends Event {
	
	public static class WorldLoadEvent extends RayTracerEvent {
		
	}
	
	public static class WorldUnloadEvent extends RayTracerEvent {
		
	}
	
	public static class ChunkModifiedEvent extends RayTracerEvent {
		private final Chunk chunk;
		
		public ChunkModifiedEvent(Chunk chunk) {
			this.chunk = chunk;
		}
		
		public Chunk getChunk() {
			return this.chunk;
		}
	}
}
