package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class Random_element {
	private int rBits = 160;
	private int qBits = 512;
	private static Field G1;
	private static Field GT;
	private static Field Zr;

	public static Element randomG1(Pairing pairing) {
		G1 = pairing.getG1();
		return G1.newRandomElement().getImmutable();
	}

	public static Element randomGT(Pairing pairing) {
		GT = pairing.getGT();
		return GT.newRandomElement().getImmutable();
	}

	public static Element randomZr(Pairing pairing) {
		Zr = pairing.getZr();
		return Zr.newRandomElement().getImmutable();
	}

}
