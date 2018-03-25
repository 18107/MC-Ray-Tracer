package core.id107.raytracer.coretransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import core.id107.raytracer.RayTracerEvent;
import mod.id107.raytracer.RenderUtil;
import mod.id107.raytracer.TextureFinder;
import mod.id107.raytracer.gui.RayTracerSettings;
import mod.id107.raytracer.world.TextureData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.Stitcher.Slot;
import net.minecraft.util.ResourceLocation;
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
	
	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.client.renderer.texture.Stitcher#doStitch() doStitch}
	 */
	public static void createTextureMap(List<Stitcher.Slot> stitchSlots) { //FIXME
		/*if (stitchSlots.size() <= 1) {
			return;
		}
		List<Stitcher.Slot> stitchList = Lists.<Stitcher.Slot>newArrayList();
		for (Stitcher.Slot slot : stitchSlots) {
			slot.getAllStitchSlots(stitchList);
		}
		
		Map<String, TextureFinder> textureMap = new HashMap<String, TextureFinder>();
		for (int i = 0; i < stitchList.size(); i++) {
			Slot slot = stitchList.get(i);
			TextureFinder texture = new TextureFinder(slot.getOriginX(),
					slot.getOriginY(), slot.width, slot.height);
			textureMap.put(slot.getStitchHolder().getAtlasSprite().getIconName(), texture);
		}
		
		TextureData.textureMap = textureMap;*/
	}
}
