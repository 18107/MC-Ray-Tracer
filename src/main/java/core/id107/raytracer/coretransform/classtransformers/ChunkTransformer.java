package core.id107.raytracer.coretransform.classtransformers;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import core.id107.raytracer.coretransform.CLTLog;
import core.id107.raytracer.coretransform.TransformerUtil;
import core.id107.raytracer.coretransform.classtransformers.name.ClassName;
import core.id107.raytracer.coretransform.classtransformers.name.MethodName;
import core.id107.raytracer.coretransform.classtransformers.name.Names;

public class ChunkTransformer extends ClassTransformer {

	@Override
	public ClassName getClassName() {
		return Names.Chunk;
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformSetBlockState = new MethodTransformer() {
			public MethodName getMethodName() {
				return Names.Chunk_setBlockState;
			}

			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				InsnList toInsert = new InsnList();
				
				//TransformerUtil.onChunkModified(this);
				toInsert.add(new VarInsnNode(ALOAD, 0)); //this
				toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(TransformerUtil.class),
						"onChunkModified", "(L" + classNode.name + ";)V", false)); //onChunkModified
				
				method.instructions.insert(toInsert);
				
			}
		};
		
		return new MethodTransformer[] {transformSetBlockState};
	}

}
