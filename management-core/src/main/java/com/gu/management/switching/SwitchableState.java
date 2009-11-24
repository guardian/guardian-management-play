package com.gu.management.switching;


public abstract class SwitchableState implements Switchable {

	private boolean isOn = true;

	@Override
	public boolean isSwitchedOn() {
		return isOn;
	}

	@Override
	public void switchOff() {
		isOn = false;
	}

	@Override
	public void switchOn() {
		isOn = true;
	}
}