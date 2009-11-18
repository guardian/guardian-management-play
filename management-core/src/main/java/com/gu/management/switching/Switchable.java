package com.gu.management.switching;


public interface Switchable extends SwitchState {
	void switchOff();
	void switchOn();
	String getDescription();
	String getName();
}