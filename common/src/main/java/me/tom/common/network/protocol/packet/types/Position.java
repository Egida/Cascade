package me.tom.common.network.protocol.packet.types;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Position {
    private final int x;
    private final int y;
    private final int z;

    public void write(ByteBuf out) {
        long packed = 0;

        long px = ((long) x & 0x3FFFFFFL);
        long pz = ((long) z & 0x3FFFFFFL);
        long py = ((long) y & 0xFFFL);

        packed |= (px << 38);
        packed |= (pz << 12);
        packed |= py;

        out.writeLong(packed);
    }

    public static Position read(ByteBuf in) {
        long packed = in.readLong();

        int x = (int) (packed >> 38);
        int z = (int) ((packed >> 12) & 0x3FFFFFFL);
        int y = (int) (packed & 0xFFFL);

        if ((z & (1 << 25)) != 0) {
            z |= ~0x3FFFFFF;
        }

        if ((y & (1 << 11)) != 0) {
            y |= ~0xFFF;
        }

        return new Position(x, y, z);
    }
}