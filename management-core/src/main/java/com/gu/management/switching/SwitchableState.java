package com.gu.management.switching;


public class SwitchableState implements Switchable {

	private boolean isOn = true;
	private String description = "default switch description";
	private String name = "default switch name";

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

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	// this method is here to configure the initial switch state in spring config and
	// should not be used by app code. Use switchOn and switchOff in app code.
	@Deprecated
	public void setInitiallySwitchedOn(boolean initialState) {
		isOn = initialState;
	}

}