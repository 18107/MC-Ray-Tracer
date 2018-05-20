package mod.id107.raytracer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrices {

	private Matrix4f x01;
	private Matrix4f x10;
	
	private Matrix4f y01;
	private Matrix4f y10;
	private Matrix4f y11;
	
	private Matrix4f z01;
	private Matrix4f z10;
	private Matrix4f z11;
	
	private Matrix4f xMirror;
	
	//FIXME only 48 matrices needed
	
	public Matrices() {
		xMirror = new Matrix4f(-1,0,0,0, 0,1,0,0, 0,0,1,0, 1,0,0,1); //flip
		
		z01 = new Matrix4f(0,1,0,0, -1,0,0,0, 0,0,1,0, 1,0,0,1); //right
		z10 = new Matrix4f(0,-1,0,0, 1,0,0,0, 0,0,1,0, 0,1,0,1); //left
		z11 = new Matrix4f(-1,0,0,0, 0,-1,0,0, 0,0,1,0, 1,1,0,1); //over
		
		y01 = new Matrix4f(0,0,1,0, 0,1,0,0, -1,0,0,0, 1,0,0,1);  //east
		y10 = new Matrix4f(0,0,-1,0, 0,1,0,0, 1,0,0,0, 0,0,1,1);  //west
		y11 = new Matrix4f(-1,0,0,0, 0,1,0,0, 0,0,-1,0, 1,0,1,1); //north
		x01 = new Matrix4f(1,0,0,0, 0,0,1,0, 0,-1,0,0, 0,1,0,1); //up
		x10 = new Matrix4f(1,0,0,0, 0,0,-1,0, 0,1,0,0, 0,0,1,1); //down
	}
	
	public String getMatrices() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nconst mat4 rotation[48] = mat4[](\n");
		for (int i = 0; i < 48; i++) {
			Matrix4f matrix = new Matrix4f(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1);
			
			switch(i>>3) {
			case 1:
				Matrix4f.mul(y01, matrix, matrix);
				break;
			case 2:
				Matrix4f.mul(y10, matrix, matrix);
				break;
			case 3:
				Matrix4f.mul(y11, matrix, matrix);
				break;
			case 4:
				Matrix4f.mul(x01, matrix, matrix);
				break;
			case 5:
				Matrix4f.mul(x10, matrix, matrix);
				break;
			}
			
			switch((i>>1)&3) {
			case 1:
				Matrix4f.mul(z01, matrix, matrix);
				break;
			case 2:
				Matrix4f.mul(z10, matrix, matrix);
				break;
			case 3:
				Matrix4f.mul(z11, matrix, matrix);
				break;
			}
			
			if ((i&1)==1) {
				Matrix4f.mul(xMirror, matrix, matrix);
			}
			
			float[] f = matrix.get();
			sb.append("  mat4(");
			for (int a = 0; a < 4; a++) {
				for (int e = 0; e < 4; e++) {
					sb.append((int)f[a*4 + e]);
					sb.append(',');
				}
				sb.append(' ');
			}
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
			sb.append("),\n");
		}
		sb.deleteCharAt(sb.length()-2);
		sb.append(");");
		Log.info(sb);
		return sb.toString();
	}
}
