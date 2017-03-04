package mod.id107.raytracer.coretransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import mod.id107.raytracer.RenderUtil;
import mod.id107.raytracer.TextureFinder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.Stitcher.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;

public class TransformerUtil {

	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.client.Minecraft#loadWorld() loadWorld}
	 */
	public static void onWorldLoad(WorldClient worldClient) {
		if (worldClient != null) {
			RenderUtil.createShader();
		} else {
			RenderUtil.destroyShader();
		}
	}
	
	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.client.renderer.EntityRenderer#renderWorld() renderWorld}
	 */
	public static void runShader() {
		RenderUtil.runShader();
	}
	
	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.world.chunk.Chunk#setBlockState() setBlockState}
	 */
	public static void onChunkModified(Chunk chunk) {
		//TODO make worldLoader private
		if (RenderUtil.worldLoader != null)
			RenderUtil.worldLoader.updateChunkFirst(chunk);
	}
	
	/**
	 * Called from asm modified code:
	 * {@link net.minecraft.client.renderer.texture.Stitcher#doStitch() doStitch}
	 */
	public static void createTextureMap(List<Stitcher.Slot> stitchSlots) {
		if (stitchSlots.size() <= 1) {
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
		
		Block block = Block.getBlockById(1);
		ResourceLocation location = block.getRegistryName();
		
		RenderUtil.textureMap = textureMap;
	}
}
