package latmod.ftbu.core;

import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IServerConfig
{
	public String getConfigName();
	public void load();
	public void readConfig(NBTTagCompound tag);
	public void writeConfig(NBTTagCompound tag, EntityPlayerMP ep);
	
	public static class Registry
	{
		private static final FastMap<String, IServerConfig> map = new FastMap<String, IServerConfig>();
		
		public static void add(IServerConfig i)
		{
			if(i != null && i.getConfigName() != null)
			{
				map.put(i.getConfigName(), i);
				if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("Added IServerConfig '" + i.getConfigName() + "'");
			}
		}
		
		public static void remove(IServerConfig i)
		{
			if(i != null && i.getConfigName() != null)
				map.remove(i.getConfigName());
		}
		
		public static void readFromNBT(NBTTagCompound tag)
		{
			for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = (NBTTagCompound)tag.getTag(map.keys.get(i));
				if(tag1 != null) map.values.get(i).readConfig(tag1);
			}
		}
		
		public static void writeToNBT(NBTTagCompound tag, EntityPlayerMP ep, String s)
		{
			if(s != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				IServerConfig c = map.get(s);
				if(c != null) c.writeConfig(tag1, ep);
				if(!tag1.hasNoTags()) tag.setTag(s, tag1);
			}
			else for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				map.values.get(i).writeConfig(tag1, ep);
				if(!tag1.hasNoTags()) tag.setTag(map.keys.get(i), tag1);
			}
		}

		public static void load()
		{
			for(int i = 0; i < map.size(); i++)
				map.values.get(i).load();
		}
		
		public static void updateConfig(EntityPlayerMP ep, String s)
		{ if(ep != null) LMNetHelper.sendTo(ep, new MessageUpdateConfig(ep, s)); }
	}
}