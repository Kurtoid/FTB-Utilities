package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbu.world.chunks.ClaimedChunk;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataMP;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataSP;
import com.latmod.lib.math.ChunkDimPos;
import com.latmod.lib.util.LMNetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class MessageAreaUpdate extends MessageToClient<MessageAreaUpdate>
{
    public int dim;
    public Map<ChunkDimPos, ClaimedChunk> types;

    public MessageAreaUpdate()
    {
    }

    public MessageAreaUpdate(int x, int z, int d, int sx, int sz)
    {
        dim = d;

        types = new HashMap<>();

        for(int x1 = x; x1 < x + sx; x1++)
        {
            for(int z1 = z; z1 < z + sz; z1++)
            {
                ChunkDimPos pos = new ChunkDimPos(x1, z1, d);
                types.put(pos, FTBUWorldDataMP.chunks.getChunk(pos));
            }
        }
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        dim = io.readInt();
        types = new HashMap<>();

        int s = io.readInt();

        for(int i = 0; i < s; i++)
        {
            int x = io.readInt();
            int z = io.readInt();
            ChunkDimPos pos = new ChunkDimPos(x, z, dim);
            boolean b = io.readBoolean();

            if(b)
            {
                ForgePlayer owner = ForgeWorldSP.inst == null ? null : ForgeWorldSP.inst.getPlayer(LMNetUtils.readUUID(io));

                if(owner != null)
                {
                    ClaimedChunk chunk = new ClaimedChunk(owner, pos);
                    chunk.loaded = io.readBoolean();
                    types.put(pos, chunk);
                }
            }
            else
            {
                types.put(pos, null);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(dim);
        io.writeInt(types.size());

        for(Map.Entry<ChunkDimPos, ClaimedChunk> e : types.entrySet())
        {
            io.writeInt(e.getKey().posX);
            io.writeInt(e.getKey().posZ);

            if(e.getValue() == null)
            {
                io.writeBoolean(false);
            }
            else
            {
                io.writeBoolean(true);
                LMNetUtils.writeUUID(io, e.getValue().owner.getProfile().getId());
                io.writeBoolean(e.getValue().loaded);
            }
        }
    }

    @Override
    public void onMessage(MessageAreaUpdate m, Minecraft mc)
    {
        if(ForgeWorldSP.inst != null)
        {
            FTBUWorldDataSP.setTypes(m.types);
        }
    }
}