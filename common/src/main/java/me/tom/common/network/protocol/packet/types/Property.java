package me.tom.common.network.protocol.packet.types;

import io.netty.buffer.ByteBuf;

public class Property {
    public String name;
    public String value;
    public String signature;
    
    public void read(ByteBuf in) {
    	this.name = Utf8String.read(in, 32767);
    	this.value = Utf8String.read(in, 32767);
    	
    	if(in.readBoolean())
    		this.signature = Utf8String.read(in, 32767);
    }
}