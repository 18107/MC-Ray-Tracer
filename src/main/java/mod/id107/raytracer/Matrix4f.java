package mod.id107.raytracer;

public class Matrix4f extends org.lwjgl.util.vector.Matrix4f {

	public Matrix4f(float...fs) {
		super.m00 = fs[0];
		super.m01 = fs[1];
		super.m02 = fs[2];
		super.m03 = fs[3];
		super.m10 = fs[4];
		super.m11 = fs[5];
		super.m12 = fs[6];
		super.m13 = fs[7];
		super.m20 = fs[8];
		super.m21 = fs[9];
		super.m22 = fs[10];
		super.m23 = fs[11];
		super.m30 = fs[12];
		super.m31 = fs[13];
		super.m32 = fs[14];
		super.m33 = fs[15];
	}
	
	public void put(float...fs) {
		super.m00 = fs[0];
		super.m01 = fs[1];
		super.m02 = fs[2];
		super.m03 = fs[3];
		super.m10 = fs[4];
		super.m11 = fs[5];
		super.m12 = fs[6];
		super.m13 = fs[7];
		super.m20 = fs[8];
		super.m21 = fs[9];
		super.m22 = fs[10];
		super.m23 = fs[11];
		super.m30 = fs[12];
		super.m31 = fs[13];
		super.m32 = fs[14];
		super.m33 = fs[15];
	}
	
	public float[] get() {
		float[] fs = new float[16];
		fs[0] = super.m00;
		fs[1] = super.m01;
		fs[2] = super.m02;
		fs[3] = super.m03;
		fs[4] = super.m10;
		fs[5] = super.m11;
		fs[6] = super.m12;
		fs[7] = super.m13;
		fs[8] = super.m20;
		fs[9] = super.m21;
		fs[10] = super.m22;
		fs[11] = super.m23;
		fs[12] = super.m30;
		fs[13] = super.m31;
		fs[14] = super.m32;
		fs[15] = super.m33;
		return fs;
	}
}
