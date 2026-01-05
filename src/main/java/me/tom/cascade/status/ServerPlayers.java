package me.tom.cascade.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerPlayers {
	private int max;
	private int online;
	
	private ServerPlayer[] sample;
}
