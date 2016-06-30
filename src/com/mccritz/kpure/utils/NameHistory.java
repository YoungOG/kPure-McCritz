package com.mccritz.kpure.utils;

import java.util.ArrayList;

import com.mccritz.kpure.utils.NameHistory.Element;

import lombok.Getter;

public class NameHistory extends ArrayList<Element> {
    /**
     * Generated.
     */
    private static final long serialVersionUID = 665280142894925580L;

    @Getter
    public static class Element {
	private final String name;
	private final Long changedToAt;

	public Element(String name, Long changedToAt) {
	    this.name = name;
	    this.changedToAt = changedToAt;
	}
    }
}