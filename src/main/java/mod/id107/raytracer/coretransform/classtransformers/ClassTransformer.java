package mod.id107.raytracer.coretransform.classtransformers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import mod.id107.raytracer.coretransform.classtransformers.name.ClassName;
import mod.id107.raytracer.coretransform.classtransformers.name.MethodName;

/**
 * Holds all of the class transformers.
 *
 */
public abstract class ClassTransformer {

	private static ClassTransformer[] transformers;
	
	static {
		//Put all of the class transformers here
		transformers = new ClassTransformer[] {new MinecraftTransformer(), new StitcherTransformer(), new GuiOptionsTransformer(), new EntityRendererTransformer(), new ChunkTransformer(), new RenderGlobalTransformer()};
	}
	
	/**
	 * @return the name of the class
	 */
	public abstract ClassName getClassName();
	
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
		public abstract void transform(ClassNode classNode, MethodNode method, boolean obfuscated);
		public abstract MethodName getMethodName();
	}
}
