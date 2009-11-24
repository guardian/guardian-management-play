package com.gu.management.switching;

public interface Switchable extends SwitchState {
	void switchOff();
	void switchOn();

    /**
     * @return a sentence that describes, in websys understandable terms, the
     * effect of switching this swictch
     */
	String getDescription();

    /**
     * @return a single url-safe word that can be used to construct urls
     * for this switch.
     */
	String getWordForUrl();
}