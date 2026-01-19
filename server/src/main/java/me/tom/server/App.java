package me.tom.server;

public class App 
{
    public static void main( String[] args )
    {
    	new DedicatedServer(Short.parseShort(args[0])).start();
    }
}
