package mod.id107.raytracer.coretransform;

import mod.id107.raytracer.RenderUtil;
import net.minecraft.client.multiplayer.WorldClient;

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
}
