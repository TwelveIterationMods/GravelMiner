package net.blay09.mods.gravelminer.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageSetEnabled implements IMessage {

	private boolean enabled;

	public MessageSetEnabled() {
	}

	public MessageSetEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		enabled = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(enabled);
	}

	public boolean isEnabled() {
		return enabled;
	}
}
