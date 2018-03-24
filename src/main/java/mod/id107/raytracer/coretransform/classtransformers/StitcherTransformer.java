package mod.id107.raytracer.coretransform.classtransformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.id107.raytracer.coretransform.CLTLog;
import mod.id107.raytracer.coretransform.CoreLoader;
import mod.id107.raytracer.coretransform.TransformerUtil;
import mod.id107.raytracer.coretransform.classtransformers.name.ClassName;
import mod.id107.raytracer.coretransform.classtransformers.name.MethodName;
import mod.id107.raytracer.coretransform.classtransformers.name.Names;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;

public class StitcherTransformer extends ClassTransformer {

	@Override
	public ClassName getClassName() {
		return Names.Stitcher;
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformDoStitch = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.Stitcher_doStitch;
			}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				AbstractInsnNode instruction = method.instructions.getLast();
				
				//find ProgressManager.pop
				for (int i = 0; i < 5; i++) {
					instruction = instruction.getPrevious();
				}
				
				InsnList toInsert = new InsnList();
				
				//TransformerUtil.createTextureMap(stitchSlots);
				toInsert.add(new VarInsnNode(ALOAD, 0)); //this
				toInsert.add(new FieldInsnNode(GETFIELD, Names.Stitcher.getInternalName(),
						Names.Stitcher_stitchSlots.getFullName(obfuscated),
						Names.Stitcher_stitchSlots.getDesc(obfuscated))); //stitchSlots
				toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(TransformerUtil.class),
						"createTextureMap", "(Ljava/util/List;)V", false)); //TransformerUtil.createTextureMap
				
				method.instructions.insertBefore(instruction, toInsert);
			}
		};
		
		return new MethodTransformer[] {transformDoStitch};
	}

}
