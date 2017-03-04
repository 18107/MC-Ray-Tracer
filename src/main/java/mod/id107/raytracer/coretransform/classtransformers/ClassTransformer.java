package mod.id107.raytracer.coretransform.classtransformers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Holds all of the class transformers.
 *
 */
public abstract class ClassTransformer {

	private static ClassTransformer[] transformers;
	
	static {
		//Put all of the class transformers here
		transformers = new ClassTransformer[] {new MinecraftTransformer(), new EntityRendererTransformer(), new StitcherTransformer(), new ChunkTransformer()};
	}
	
	/**
	 * @return the obfuscated name of the class
	 */
	public abstract String getObfuscatedClassName();
	
	/**
	 * @return the name of the class
	 */
	public abstract String getClassName();
	
	/**
	 * @return an array containing all method transformers for this class transformer
	 */
	public abstract MethodTransformer[] getMethodTransformers();
	
	/**
	 * @return an array containing all class transformers
	 */
	public static ClassTransformer[] getClassTransformers() {
		return transformers;
	}
	
	//Template for a method transformer
	public static abstract class MethodTransformer {
		public abstract String getMethodName();
		public abstract String getDescName();
		public abstract void transform(ClassNode classNode, MethodNode method, boolean obfuscated);
	}
}
