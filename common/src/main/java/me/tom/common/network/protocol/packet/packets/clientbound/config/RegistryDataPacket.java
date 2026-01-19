package me.tom.common.network.protocol.packet.packets.clientbound.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import me.tom.common.network.protocol.packet.Packet;
import me.tom.common.network.protocol.packet.types.Utf8String;
import me.tom.common.network.protocol.packet.types.VarInt;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

@Getter
public class RegistryDataPacket implements Packet {

    private final String registryId;
    private final List<Entry> entries = new ArrayList<>();

    public static class Entry {
        public final String id;
        public final CompoundTag nbt;

        public Entry(String id, CompoundTag nbt) {
            this.id = id;
            this.nbt = nbt;
        }
        
        public Entry(String id) {
            this.id = id;
			this.nbt = null;
        }
    }

    public RegistryDataPacket(String registryId, Entry... entries) {
        this.registryId = registryId;
        this.entries.addAll(Arrays.asList(entries));
    }

    @Override
    public void encode(ByteBuf out) throws Exception {
        Utf8String.write(out, registryId, 32767);

        VarInt.write(out, entries.size());
        
        for (Entry e : entries) {
            Utf8String.write(out, e.id, 32767);

            if (e.nbt == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                byte[] nbtBytes = toNbtBytes(e.nbt);
                out.writeBytes(nbtBytes);
            }
        }
    }
    
    private static byte[] toNbtBytes(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (NBTOutputStream nbtOut = new NBTOutputStream(baos)) {
            nbtOut.writeTag(new NamedTag("", tag), Integer.MAX_VALUE);
        }
        return baos.toByteArray();
    }


    @Override
    public void decode(ByteBuf in) {
    	
    }
}