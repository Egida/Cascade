package me.tom.cascade.net.handlers;

import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.tom.cascade.net.crypto.AesDecryptHandler;
import me.tom.cascade.net.crypto.AesEncryptHandler;
import me.tom.cascade.protocol.ProtocolAttributes;
import me.tom.cascade.protocol.packet.packets.s2c.EncryptionResponsePacket;
import me.tom.cascade.util.Crypto;

public class EncryptionResponseHandler extends SimpleChannelInboundHandler<EncryptionResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EncryptionResponsePacket packet) {

        try {
            PrivateKey privateKey = Crypto.KEY_PAIR.getPrivate();

            byte[] sharedSecret = Crypto.rsaDecrypt(packet.getSharedSecret(), privateKey);

            byte[] token = Crypto.rsaDecrypt(packet.getVerifyToken(), privateKey);

            byte[] expected = ctx.channel().attr(ProtocolAttributes.VERIFY_TOKEN).get();
            if (!Arrays.equals(token, expected)) {
                ctx.close();
                return;
            }

            SecretKey aesKey = new SecretKeySpec(sharedSecret, "AES");

            ctx.pipeline().addBefore("packet-decoder", "decrypt", new AesDecryptHandler(aesKey));
            ctx.pipeline().addBefore("packet-encoder", "encrypt", new AesEncryptHandler(aesKey));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.close();
        }
    }
}