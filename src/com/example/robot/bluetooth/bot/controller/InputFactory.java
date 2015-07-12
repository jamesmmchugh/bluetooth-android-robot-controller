package com.example.robot.bluetooth.bot.controller;

import static com.example.robot.bluetooth.bot.controller.constant.MessageType.messageTypeOf;

import com.example.robot.bluetooth.bot.controller.constant.MessageType;

public class InputFactory {

	public static final byte COMMAND_TYPE_MASK = -64;
	public static final byte PAYLOAD_MASK = 127;


	public ArdMessage readMessage(final int input) {
		int messageTypeId = (input & COMMAND_TYPE_MASK) >> 6;
		MessageType messageType = messageTypeOf(messageTypeId);
		int payload = input & PAYLOAD_MASK;

		return new ArdMessage(messageType, payload);
	}

	public static class ArdMessage {
		public final MessageType messageType;
		public final int payload;

		public ArdMessage(MessageType messageType, int payload) {
			this.messageType = messageType;
			this.payload = payload;
		}
	}
}
