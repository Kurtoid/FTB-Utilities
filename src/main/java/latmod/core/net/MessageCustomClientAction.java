package latmod.core.net;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomClientAction implements IMessage, IMessageHandler<MessageCustomClientAction, IMessage>
{
	public String action;
	public NBTTagCompound extraData;
	
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(String s, NBTTagCompound tag)
	{
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		NBTTagCompound tag = LMNetHandler.readNBTTagCompound(data);
		
		action = tag.getString("Action");
		extraData = (NBTTagCompound)tag.getTag("Data");
	}
	
	public void toBytes(ByteBuf data)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("Action", action);
		if(extraData != null) tag.setTag("Data", extraData);
		
		LMNetHandler.writeNBTTagCompound(data, tag);
	}
	
	public IMessage onMessage(MessageCustomClientAction message, MessageContext ctx)
	{ new CustomActionEvent(ctx.getServerHandler().playerEntity, message.action, message.extraData, Side.SERVER).post(); return null; }
}