package me.tom.cascade.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerVersion {
	private String name;
	private int protocol;
}
