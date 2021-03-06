package core.id107.raytracer.coretransform.classtransformers;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NOP;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import core.id107.raytracer.coretransform.CLTLog;
import core.id107.raytracer.coretransform.classtransformers.name.ClassName;
import core.id107.raytracer.coretransform.classtransformers.name.MethodName;
import core.id107.raytracer.coretransform.classtransformers.name.Names;
import mod.id107.raytracer.RenderUtil;

public class RenderGlobalTransformer extends ClassTransformer {

	@Override
	public ClassName getClassName() {
		return Names.RenderGlobal;
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		//TODO renderParticles
		
		MethodTransformer transformRenderEntities = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.RenderGlobal_renderEntities;
			}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderEntities()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				//method.instructions.insert(skipMethod(RETURN, NOP)); FIXME renderManager.options==null, renderManager.cacheActiveRenderInfo(
			}
		};
		
		//TODO transformSetupTerrain
		
		MethodTransformer transformRenderBlockLayer = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.RenderGlobal_renderBlockLayer;
			}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderBlockLayer()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(IRETURN, ICONST_0));
			}
		};
		
		MethodTransformer transformRenderSky = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.RenderGlobal_renderSky;
			}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#renderSky()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		MethodTransformer transformRenderClouds = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.RenderGlobal_renderClouds;
			}
			
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
			public MethodName getMethodName() {
				return Names.RenderGlobal_drawBlockDamageTexture;
			}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.RenderGlobal#drawBlockDamageTexture()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				method.instructions.insert(skipMethod(RETURN, NOP));
			}
		};
		
		MethodTransformer transformDrawSelectionBox = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.RenderGlobal_drawSelectionBox;
			}
			
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
