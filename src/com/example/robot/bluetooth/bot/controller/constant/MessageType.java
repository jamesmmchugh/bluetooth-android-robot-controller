package com.example.robot.bluetooth.bot.controller.constant;

import static java.lang.String.format;

public enum MessageType {
	DEBUG_MESSAGE(1),
	BUMPER_PRESSED(2);

	private final int ordinal;

	MessageType(int ordinal) {
		this.ordinal = ordinal;
	}

	public static MessageType messageTypeOf(Integer ordinal) {
		for(MessageType messageType : values()) {
			if(messageType.ordinal == ordinal) {
				return messageType;
			}
		}
		throw new IllegalArgumentException(format("No commandType for %s", ordinal));
	}
}
