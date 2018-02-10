package utils;

public enum CollisionGroup {
	WORLD(0x01), POWER(0x02);

	public final int BITS;
	
	CollisionGroup(int bits) {
		BITS = bits;
	}
	
}
