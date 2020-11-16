package ru.windcorp.progressia.common.util;

import java.util.function.Consumer;

import glm.mat._4.Mat4;
import glm.vec._2.Vec2;
import glm.vec._2.d.Vec2d;
import glm.vec._2.i.Vec2i;
import glm.vec._3.Vec3;
import glm.vec._3.d.Vec3d;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import glm.vec._4.d.Vec4d;
import glm.vec._4.i.Vec4i;

public class VectorUtil {
	
	public static enum Axis {
		X, Y, Z, W;
	}
	
	public static void forEachVectorInCuboid(
			int x0, int y0, int z0,
			int x1, int y1, int z1,
			Consumer<Vec3i> action
	) {
		Vec3i cursor = Vectors.grab3i();
		
		for (int x = x0; x < x1; ++x) {
			for (int y = y0; y < y1; ++y) {
				for (int z = z0; z < z1; ++z) {
					cursor.set(x, y, z);
					action.accept(cursor);
				}
			}
		}
		
		Vectors.release(cursor);
	}
	
	public static void applyMat4(Vec3 in, Mat4 mat, Vec3 out) {
		Vec4 vec4 = Vectors.grab4();
		vec4.set(in, 1f);
		
		mat.mul(vec4);
		
		out.set(vec4.x, vec4.y, vec4.z);
		Vectors.release(vec4);
	}
	
	public static void applyMat4(Vec3 inOut, Mat4 mat) {
		Vec4 vec4 = Vectors.grab4();
		vec4.set(inOut, 1f);
		
		mat.mul(vec4);
		
		inOut.set(vec4.x, vec4.y, vec4.z);
	}
	
	public static Vec3 linearCombination(
			Vec3 va, float ka,
			Vec3 vb, float kb,
			Vec3 output
	) {
		output.set(
				va.x * ka + vb.x * kb,
				va.y * ka + vb.y * kb,
				va.z * ka + vb.z * kb
		);
		return output;
	}
	
	public static Vec3 linearCombination(
			Vec3 va, float ka,
			Vec3 vb, float kb,
			Vec3 vc, float kc,
			Vec3 output
	) {
		output.set(
				va.x * ka + vb.x * kb + vc.x * kc,
				va.y * ka + vb.y * kb + vc.y * kc,
				va.z * ka + vb.z * kb + vc.z * kc
		);
		return output;
	}
	
	public static float get(Vec2 v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		default: throw new IllegalArgumentException("Vec2 does not have axis " + a);
		}
	}

	public static Vec2 set(Vec2 v, Axis a, float value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		default: throw new IllegalArgumentException("Vec2 does not have axis " + a);
		}
		return v;
	}

	public static int get(Vec2i v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		default: throw new IllegalArgumentException("Vec2i does not have axis " + a);
		}
	}

	public static Vec2i set(Vec2i v, Axis a, int value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		default: throw new IllegalArgumentException("Vec2i does not have axis " + a);
		}
		return v;
	}

	public static double get(Vec2d v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		default: throw new IllegalArgumentException("Vec2d does not have axis " + a);
		}
	}

	public static Vec2d set(Vec2d v, Axis a, double value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		default: throw new IllegalArgumentException("Vec2d does not have axis " + a);
		}
		return v;
	}

	public static float get(Vec3 v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		case Z: return v.z;
		default: throw new IllegalArgumentException("Vec3 does not have axis " + a);
		}
	}

	public static Vec3 set(Vec3 v, Axis a, float value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		case Z: v.z = value; break;
		default: throw new IllegalArgumentException("Vec3 does not have axis " + a);
		}
		return v;
	}

	public static int get(Vec3i v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		case Z: return v.z;
		default: throw new IllegalArgumentException("Vec3i does not have axis " + a);
		}
	}

	public static Vec3i set(Vec3i v, Axis a, int value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		case Z: v.z = value; break;
		default: throw new IllegalArgumentException("Vec3i does not have axis " + a);
		}
		return v;
	}

	public static double get(Vec3d v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		case Z: return v.z;
		default: throw new IllegalArgumentException("Vec3d does not have axis " + a);
		}
	}

	public static Vec3d set(Vec3d v, Axis a, double value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		case Z: v.z = value; break;
		default: throw new IllegalArgumentException("Vec3d does not have axis " + a);
		}
		return v;
	}

	public static float get(Vec4 v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		case Z: return v.z;
		case W: return v.w;
		default: throw new IllegalArgumentException("Vec4 does not have axis " + a);
		}
	}

	public static Vec4 set(Vec4 v, Axis a, float value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		case Z: v.z = value; break;
		case W: v.w = value; break;
		default: throw new IllegalArgumentException("Vec4 does not have axis " + a);
		}
		return v;
	}

	public static int get(Vec4i v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		case Z: return v.z;
		case W: return v.w;
		default: throw new IllegalArgumentException("Vec4i does not have axis " + a);
		}
	}

	public static Vec4i set(Vec4i v, Axis a, int value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		case Z: v.z = value; break;
		case W: v.w = value; break;
		default: throw new IllegalArgumentException("Vec4i does not have axis " + a);
		}
		return v;
	}

	public static double get(Vec4d v, Axis a) {
		switch (a) {
		case X: return v.x;
		case Y: return v.y;
		case Z: return v.z;
		case W: return v.w;
		default: throw new IllegalArgumentException("Vec4d does not have axis " + a);
		}
	}

	public static Vec4d set(Vec4d v, Axis a, double value) {
		switch (a) {
		case X: v.x = value; break;
		case Y: v.y = value; break;
		case Z: v.z = value; break;
		case W: v.w = value; break;
		default: throw new IllegalArgumentException("Vec4d does not have axis " + a);
		}
		return v;
	}
	
	private VectorUtil() {}

}
