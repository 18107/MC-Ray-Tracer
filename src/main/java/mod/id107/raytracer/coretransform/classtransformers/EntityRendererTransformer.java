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
import net.minecraft.client.renderer.GlStateManager;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;

public class EntityRendererTransformer extends ClassTransformer {

	@Override
	public String getObfuscatedClassName() {
		return "bqc";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.client.renderer.EntityRenderer";
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformSetupCameraTransform = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "setupCameraTransform";}
			public String getDescName() {return "(FI)V";}
			
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
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(GlStateManager.class),
								obfuscated ? "func_179109_b" : "translate", "(FFF)V", false));
						
						method.instructions.insertBefore(instruction, toInsert);
						
						break;
					}
				}
			}
		};
		
		//FIXME
		MethodTransformer transformRenderWorld = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderWorldPass";}
			public String getDescName() {return "(IFJ)V";}
			
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				AbstractInsnNode instruction = method.instructions.getLast();
				for (int i = 0; i < 5; i++) {
					instruction = instruction.getPrevious();
				}
				
				method.instructions.insert(instruction, new MethodInsnNode(INVOKESTATIC,
						Type.getInternalName(TransformerUtil.class), "runShader", "()V", false));
			}
			
		};
		
		MethodTransformer transformRenderWorldPass = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "renderWorldPass";}
			public String getDescName() {return "(IFJ)V";}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					
					//find renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, (double)partialTicks, pass, entity);
					if (instruction.getOpcode() == F2D &&
						instruction.getNext().getOpcode() == ILOAD) {
						CLTLog.info("found F2D in method " + getMethodName());
						
						//Go to start of method call
						for (int i = 0; i < 4+3; i++) {
							instruction = instruction.getPrevious();
						}
						
						//remove method call
						for (int i = 0; i < 8+3+41; i++) {
							method.instructions.remove(instruction.getNext());
						}
						
						break;
					}
				}
			}
		};
		
		return new MethodTransformer[] {transformSetupCameraTransform, transformRenderWorld, transformRenderWorldPass};
	}

}
