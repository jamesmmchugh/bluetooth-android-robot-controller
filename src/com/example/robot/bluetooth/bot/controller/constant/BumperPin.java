package com.example.robot.bluetooth.bot.controller.constant;

import static java.lang.String.format;

public enum BumperPin {
	FC(13),
	BC(12),
	FR(2),
	FL(4),
	BL(7),
	BR(8);


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
