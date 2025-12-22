package me.tom.cascade.net.crypto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AesEncryptHandler extends MessageToByteEncoder<ByteBuf> {

    private final Cipher cipher;

    public AesEncryptHandler(SecretKey key) {
        this.cipher = me.tom.cascade.util.Crypto.createAesCipher(Cipher.ENCRYPT_MODE, key);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        byte[] input = new byte[in.readableBytes()];
        in.readBytes(input);

        byte[] encrypted = cipher.update(input);
        out.writeBytes(encrypted);
    }
}