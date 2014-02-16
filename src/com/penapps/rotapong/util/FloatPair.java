package com.penapps.rotapong.util;

public final class FloatPair {	

	public float first, second;
	
	public FloatPair(float first, float second) {
		this.first = first;
		this.second = second;
	}
	
	public FloatPair() {
		this(0.0f, 0.0f);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(first);
		result = prime * result + Float.floatToIntBits(second);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatPair other = (FloatPair) obj;
		if (Float.floatToIntBits(first) != Float.floatToIntBits(other.first))
			return false;
		if (Float.floatToIntBits(second) != Float.floatToIntBits(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
	
	

}
