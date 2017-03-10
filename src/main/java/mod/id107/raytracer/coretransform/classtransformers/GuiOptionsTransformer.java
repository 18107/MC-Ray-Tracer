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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;

import org.objectweb.asm.Type;

public class GuiOptionsTransformer extends ClassTransformer {

	@Override
	public String getObfuscatedClassName() {return "bhg";}

	@Override
	public String getClassName() {return "net.minecraft.client.gui.GuiOptions";}

	@Override
	public MethodTransformer[] getMethodTransformers() {
		
		MethodTransformer transformInitGui = new MethodTransformer() {
			
			@Override
			public String getMethodName() {return CoreLoader.isObfuscated ? "b" : "initGui";}
			
			@Override
			public String getDescName() {return "()V";}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				AbstractInsnNode instruction = method.instructions.toArray()[2];
				CLTLog.info("Starting at begining of method " + getMethodName());
				
				InsnList toInsert = new InsnList();
				toInsert.add(new VarInsnNode(ALOAD, 0)); //this
				toInsert.add(new VarInsnNode(ALOAD, 0)); //this
				toInsert.add(new FieldInsnNode(GETFIELD, classNode.name,
					obfuscated ? "field_146292_n" : "buttonList", "L" + Type.getInternalName(List.class) + ";")); //buttonList
				toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(TransformerUtil.class),
					"addButtonsToGuiOptions", "(L" + classNode.name + ";L" + Type.getInternalName(List.class) + ";)V", false));
				
				method.instructions.insert(instruction, toInsert);
			}
		};
		
		MethodTransformer transformActionPerformed = new MethodTransformer() {
			
			@Override
			public String getMethodName() {return CoreLoader.isObfuscated ? "a" : "actionPerformed";}
			
			@Override
			public String getDescName() {return "(L" + (CoreLoader.isObfuscated ? "bfk" : Type.getInternalName(GuiButton.class)) + ";)V";}
			
			@Override
			public void transform(ClassNode classNode, MethodNode method, boolean obfuscated) {
				CLTLog.info("Found method: " + method.name + " " + method.desc);
				
				AbstractInsnNode instruction = method.instructions.toArray()[2];
				CLTLog.info("Starting at begining of method " + getMethodName());
				
				InsnList toInsert = new InsnList();
				
				toInsert.add(new VarInsnNode(ALOAD, 0)); //this
				toInsert.add(new VarInsnNode(ALOAD, 1)); //button
				toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(TransformerUtil.class),
						"addButtonAction", "(L" + classNode.name + ";L" + Type.getInternalName(GuiButton.class) + ";)V", false));
				
				method.instructions.insert(instruction, toInsert);
			}
		};
		
		return new MethodTransformer[] {transformInitGui, transformActionPerformed};
	}

}
