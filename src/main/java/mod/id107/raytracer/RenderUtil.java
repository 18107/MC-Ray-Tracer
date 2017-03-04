package mod.id107.raytracer;

import java.nio.IntBuffer;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import mod.id107.raytracer.world.WorldLoader;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class RenderUtil {

	private static Shader shader;
	public static WorldLoader worldLoader; //TODO multiple dimensions
	
	public static Map<String, TextureFinder> textureMap = null;
	
	public static void createShader() {
		if (shader == null) {
			shader = new Shader();
			shader.createShaderProgram();
			worldLoader = new WorldLoader(Minecraft.getMinecraft().theWorld);
		}
	}
	
	public static void destroyShader() {
		if (shader != null) {
			worldLoader = null;
			shader.deleteShaderProgram();
			shader = null;
		}
	}
	
	public static void runShader() {
		Minecraft mc = Minecraft.getMinecraft();
		
		//TODO remove
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
			destroyShader();
			createShader();
		}
		
		if (mc.theWorld != worldLoader.theWorld) {
			worldLoader = new WorldLoader(mc.theWorld);
		}
		
		//Use shader program
		GL20.glUseProgram(shader.getShaderProgram());
		
		//Setup view
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(-1, 1, -1, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		
		//TODO third person view
		Entity entity = mc.getRenderViewEntity();
		float partialTicks = mc.getRenderPartialTicks();
		double entityPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double entityPosY = entity.lastTickPosY + entity.getEyeHeight() + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double entityPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        float fov = (float) Math.toRadians(mc.entityRenderer.getFOVModifier(partialTicks, true));
		
		//Set uniform values
		int texUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "tex");
		GL20.glUniform1i(texUniform, 0);
		int cameraPosUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "cameraPos");
		GL20.glUniform3f(cameraPosUniform, (float)entityPosX%16, (float)entityPosY%16, (float)entityPosZ%16);
		int cameraDirUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "cameraDir");
		GL20.glUniform3f(cameraDirUniform, -(float)Math.toRadians(entity.rotationPitch), (float)Math.toRadians(180+entity.rotationYaw), 0);
		int fovyUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "fovy");
		GL20.glUniform1f(fovyUniform, fov);
		int fovxUniform = GL20.glGetUniformLocation(shader.getShaderProgram(), "fovx");
		GL20.glUniform1f(fovxUniform, fov*Display.getWidth()/(float)Display.getHeight());
		
		worldLoader.updateWorld(entityPosX, entityPosY, entityPosZ, shader);
		
		//Bind vbo and texture
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, shader.getVbo());
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_BYTE, false, 0, 0L);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 8);
		
		//Render
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		
		//Reset vbo and texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		//Reset view
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		
		//Stop using shader program
		GL20.glUseProgram(0);
	}
	
	//TODO move to another class
	public static void uploadIDs() {
		int idSize = Block.REGISTRY.getKeys().size();
		IntBuffer buffer = BufferUtils.createIntBuffer(idSize*16*8*4); //metadata,side,struct
		//TODO 0
		for (int i = 1; i < idSize; i++) {
			Block.getBlockById(i).getRegistryName();
		}
	}
}
