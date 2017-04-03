package mod.id107.raytracer.coretransform.classtransformers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.Type;

import mod.id107.raytracer.RenderUtil;
import mod.id107.raytracer.coretransform.CLTLog;
import mod.id107.raytracer.coretransform.CoreLoader;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.RayTraceResult;

import static org.objectweb.asm.Opcodes.*;

public class RenderGlobalTransformer extends ClassTransformer {

	@Override
	public String getObfuscatedClassName() {
		return "bqm";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.client.renderer.RenderGlobal";
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		//TODO renderParticles
		
		MethodTransformer transformRenderEntities = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderEntities";}
			public String getDescName() {return "(L" + Type.getInternalName(Entity.class) + ";L" +
					Type.getInternalName(ICamera.class) + ";F)V";}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderEntities()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		//TODO transformSetupTerrain
		
		MethodTransformer transformRenderBlockLayer = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderBlockLayer";}
			public String getDescName() {return "(L" + Type.getInternalName(BlockRenderLayer.class) +
					";DIL" + Type.getInternalName(Entity.class) + ";)I";}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderBlockLayer()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(IRETURN, ICONST_0));
			}
		};
		
		MethodTransformer transformRenderSky = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderSky";}
			public String getDescName() {return "(FI)V";}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderSky()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		MethodTransformer transformRenderClouds = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderClouds";}
			public String getDescName() {return "(FIDDD)V";}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderClouds()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		//TODO transformRenderWorldBorder
		
		MethodTransformer transformDrawBlockDamageTexture = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "drawBlockDamageTexture";}
			public String getDescName() {return "(L" + Type.getInternalName(Tessellator.class) +
					";L" + Type.getInternalName(VertexBuffer.class) + ";L" +
					Type.getInternalName(Entity.class) + ";F)V";}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#drawBlockDamageTexture()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		MethodTransformer transformDrawSelectionBox = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "drawSelectionBox";}
			public String getDescName() {return "(L" + Type.getInternalName(EntityPlayer.class) +
					";L" + Type.getInternalName(RayTraceResult.class) + ";IF)V";}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#drawSelectionBox()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		return new MethodTransformer[] {transformRenderEntities, transformRenderBlockLayer, transformRenderSky, transformRenderClouds, transformDrawBlockDamageTexture, transformDrawSelectionBox};
	}
	
	private InsnList skipMethod(int returnType, int returnValue) {
		InsnList toInsert = new InsnList();
		LabelNode label = new LabelNode();
		
		//if (rayTracingEnabled) return;
		toInsert.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(RenderUtil.class), "rayTracingEnabled", "Z"));
		toInsert.add(new JumpInsnNode(IFEQ, label));
		if (returnType != RETURN) {
			toInsert.add(new InsnNode(returnValue));
		}
		toInsert.add(new InsnNode(returnType));
		toInsert.add(label);
		
		return toInsert;
	}

}
