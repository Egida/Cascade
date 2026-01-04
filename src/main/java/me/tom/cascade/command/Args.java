package me.tom.cascade.command;

public class Args {
    public static String get(String[] args, String key) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--" + key) && i + 1 < args.length) {
                return args[i + 1];
            }
        }

        throw new IllegalArgumentException("Missing required argument: --" + key);
    }
}