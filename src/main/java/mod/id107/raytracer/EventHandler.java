package mod.id107.raytracer;

import java.lang.reflect.Field;
import java.util.Iterator;

import core.id107.raytracer.RayTracerEvent;
import mod.id107.raytracer.gui.RayTracerSettings;
import mod.id107.raytracer.world.TextureData;
import mod.id107.raytracer.world.WorldLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
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
			RenderUtil.worldLoader.updateChunkFirst(e.getChunk());
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
	
	/*@SubscribeEvent FIXME
	public void modelBake(ModelBakeEvent e) {
		IRegistry<ModelResourceLocation, IBakedModel> registry = e.getModelRegistry();
		Iterator<IBakedModel> iterator = registry.iterator();
		while (iterator.hasNext()) {
			IBakedModel iModel = iterator.next();
			ModelBlock model = null;
			try {
				Class c = iModel.getClass();
				//Only exists in ModelLoader$VanillaModelWrapper
				Field f = c.getDeclaredField("this$1");
				f.setAccessible(true);
				Object obj = f.get(iModel);
				c = obj.getClass();
				Field field = c.getDeclaredField("model");
				field.setAccessible(true);
				model = (ModelBlock) field.get(obj);
			} catch (NoSuchFieldException e1) {
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
			if (model != null) {
				if (model.getParentLocation() != null) {
					if (model.getParentLocation().getResourcePath().equals("block/cube_all")) {
						String all = model.textures.get("all");
						String key = model.getParentLocation().getResourceDomain() + ":" + all;
						TextureFinder texture = TextureData.textureMap.get(key);
						//TODO convert model to IBlockState
						//CLTLog.info("remove this line");
					}
				}
			}
			Log.info("remove this line");
		}
	}*/
	
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
