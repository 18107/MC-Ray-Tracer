package mod.id107.raytracer.coretransform.classtransformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import mod.id107.raytracer.coretransform.CLTLog;
import mod.id107.raytracer.coretransform.CoreLoader;
import mod.id107.raytracer.coretransform.TransformerUtil;
import mod.id107.raytracer.coretransform.classtransformers.name.ClassName;
import mod.id107.raytracer.coretransform.classtransformers.name.MethodName;
import mod.id107.raytracer.coretransform.classtransformers.name.Names;
import net.minecraft.client.renderer.GlStateManager;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;

public class EntityRendererTransformer extends ClassTransformer {

	@Override
	public ClassName getClassName() {
		return Names.EntityRenderer;
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformSetupCameraTransform = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.EntityRenderer_setupCameraTransform;
			}
			
			/**
			 * Transforms {@link net.minecraft.client.renderer.EntityRenderer#setupCameraTransform()}
			 */
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == TABLESWITCH) {
						CLTLog.info("Found tableswitch in method " + getMethodName());
						
						//Go back to orientCamera
						for (int i = 0; i < 8+4; i++) {
							instruction = instruction.getPrevious();
						}
						InsnList toInsert = new InsnList();
						
						//GlStateManager.translate(0, 0, -0.05F);
						toInsert.add(new InsnNode(FCONST_0)); //0
						toInsert.add(new InsnNode(FCONST_0)); //0
						toInsert.add(new LdcInsnNode(-0.05f)); //-0.05
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Names.GlStateManager.getInternalName(obfuscated),
								Names.GlStateManager_translate.getFullName(obfuscated), "(FFF)V", false));
						
						method.instructions.insertBefore(instruction, toInsert);
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformSetupCameraTransform};
	}

}
