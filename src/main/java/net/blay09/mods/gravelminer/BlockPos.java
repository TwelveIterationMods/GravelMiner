package net.blay09.mods.gravelminer;

public class BlockPos {

	private int x;
	private int y;
	private int z;

	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos(BlockPos pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BlockPos blockPos = (BlockPos) o;
		return x == blockPos.x && y == blockPos.y && z == blockPos.z;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}
}
