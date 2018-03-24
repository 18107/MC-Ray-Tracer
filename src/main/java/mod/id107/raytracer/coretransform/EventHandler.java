package mod.id107.raytracer.coretransform;

import java.lang.reflect.Field;
import java.util.Iterator;

import mod.id107.raytracer.RenderUtil;
import mod.id107.raytracer.TextureFinder;
import mod.id107.raytracer.world.TextureData;
import mod.id107.raytracer.world.WorldLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
	
	//@SubscribeEvent
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
			CLTLog.info("remove this line");
		}
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
	
	/**
	 * Runs the ray tracer.
	 * @param e
	 */
	@SubscribeEvent
	public void runShader(RenderWorldLastEvent e) {
		RenderUtil.runShader();
	}
}
