package com.example.robot.bluetooth.bot.controller.constant;

import static java.lang.String.format;

public enum BumperPin {
	FL(8),
	FC(13),
	FR(7),
	BL(2),
	BC(5),
	BR(4);

	private final int ordinal;

	BumperPin(int ordinal) {
		this.ordinal = ordinal;
	}

	public static BumperPin bumperPinOf(Integer ordinal) {
		for(BumperPin pin : values()) {
			if(pin.ordinal == ordinal) {
				return pin;
			}
		}
		throw new IllegalArgumentException(format("No commandType for %s", ordinal));
	}
}
