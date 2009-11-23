package com.gu.management.switching;


import junit.framework.TestCase;


public class SwitchableStateTest extends TestCase {

	public void testShouldDefaultToSwitchedOn() throws Exception {
		SwitchableState state = new SwitchableState();
		assertTrue(state.isSwitchedOn());
	}

	public void testShouldSwitchOnState() throws Exception {
		SwitchableState state = new SwitchableState();
		state.switchOff();
		state.switchOn();
		assertTrue(state.isSwitchedOn());
	}
	
	public void testShouldSwitchOffState() throws Exception {
		SwitchableState state = new SwitchableState();
		state.switchOn();
		state.switchOff();
		assertFalse(state.isSwitchedOn());
	}
}
