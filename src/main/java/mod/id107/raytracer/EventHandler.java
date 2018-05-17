package mod.id107.raytracer;

import core.id107.raytracer.RayTracerEvent;
import mod.id107.raytracer.gui.RayTracerSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	
	@SubscribeEvent
	public void worldLoad(RayTracerEvent.WorldLoadEvent e) {
		RenderUtil.createShader();
	}
	
	@SubscribeEvent
	public void worldUnload(RayTracerEvent.WorldUnloadEvent e) {
		RenderUtil.destroyShader();
	}
	
	@SubscribeEvent
	public void chunkModified(RayTracerEvent.ChunkModifiedEvent e) {
		if (RenderUtil.worldLoader != null) {
			RenderUtil.worldLoader.onChunkModified(e.getChunk());
		}
	}
	
	@SubscribeEvent
	public void initGui(InitGuiEvent.Post e) {
		if (e.getGui() instanceof GuiOptions) {
			e.getButtonList().add(new GuiButton(18107, e.getGui().width / 2 + 5, e.getGui().height / 6 + 12 + 6, 150, 20, "Ray Tracer Settings"));
		}
	}
	
	@SubscribeEvent
	public void actionPerformed(ActionPerformedEvent.Pre e) {
		if (e.getGui() instanceof GuiOptions && e.getButton().id == 18107) {
			e.getGui().mc.gameSettings.saveOptions();
			e.getGui().mc.displayGuiScreen(new RayTracerSettings(e.getGui()));
		}
	}
	
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
		Log.info("dimension change");
		RenderUtil.pauseRendering = true;
	}
	
	/**
	 * Reloads the world when the player changes dimension.
	 * @param e
	 */
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		Log.info("World load " + e.getWorld().provider.getDimension());
		if (RenderUtil.worldLoader != null) {
			RenderUtil.pauseRendering = false;
			RenderUtil.worldLoader.setReloadWorld();
		}
	}
	
	/**
	 * Runs the ray tracer.
	 * @param e
	 */
	@SubscribeEvent
	public void runShader(RenderWorldLastEvent e) {
		RenderUtil.runShader();
	}
}
