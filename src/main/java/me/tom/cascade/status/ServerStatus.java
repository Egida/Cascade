package me.tom.cascade.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerStatus {
	private ServerVersion version;
	private ServerPlayers players;
	private ServerDescription description;
}
