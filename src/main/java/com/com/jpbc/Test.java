package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.math.BigInteger;

public class Test {
	public static void main(String[] args) {
		int rBits = 160;
		int qBits = 512;
		Element x, y = null;
		Field Zr;
		Pairing pairing;
		pairing = PairingFactory.getPairing("a.properties");
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		Zr = pairing.getZr();
		x = Zr.newRandomElement().getImmutable();// 生成私钥
		y = Zr.newRandomElement().getImmutable();
		System.out.println(x);
		Element[] h = new Element[3];
		Element[][] t = new Element[3][3];
		for (int i = 0; i < h.length; i++) {
			h[i] = x.pow(BigInteger.valueOf(i + 1));
		}
		System.out.println("================================");
		for (int i = 0; i < h.length; i++) {
			System.out.println(h[i]);
		}
		System.out.println("================================");
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				t[i][j] = h[i].pow(BigInteger.valueOf(j + 1));
			}
		}
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				System.out.println(t[i][j]);
			}
		}
		System.out.println("================================");
		t = fixElement(h);

	}

	public static Element[][] fixElement(Element[] h) {
		int length = 3;
		Element[][] t = new Element[length][length];

		for (int i = 0; i < h.length; i++) {
			System.out.println(h[i]);
		}
		System.out.println("================================");
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				t[i][j] = h[i].pow(BigInteger.valueOf(j + 1));
			}
		}
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				System.out.println(t[i][j]);
			}
		}
		return t;
	}
}
