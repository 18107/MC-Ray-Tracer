package mod.id107.raytracer.coretransform.classtransformers;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mod.id107.raytracer.coretransform.CLTLog;
import mod.id107.raytracer.coretransform.CoreLoader;
import mod.id107.raytracer.coretransform.TransformerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;

import static org.objectweb.asm.Opcodes.*;

public class ChunkTransformer extends ClassTransformer {

	@Override
	public String getObfuscatedClassName() {
		return "aum";
	}

	@Override
	public String getClassName() {
		return "net.minecraft.world.chunk.Chunk";
	}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformSetBlockState = new MethodTransformer() {
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "setBlockState";}
			public String getDescName() {
				if (CoreLoader.isObfuscated) {
					return "(Lco;Latj;)Latj;";
				} else {
					return "(L" + Type.getInternalName(BlockPos.class) +
							";L" + Type.getInternalName(IBlockState.class) +
							";)L" + Type.getInternalName(IBlockState.class) + ";";
				}
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
