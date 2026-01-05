package me.tom.cascade.status;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerPlayer {
	private String name;
	private UUID id;
}