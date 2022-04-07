package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Test3 {
	public static void main(String[] args) {
		Element h, Y1, Y2, Y, u, v;
		Field G1, GT, Zr;

		Pairing pairing;
		pairing = PairingFactory.getPairing("a.properties");
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		Zr = pairing.getZr();
		G1 = pairing.getG1();
		GT = pairing.getGT();
		// 追溯中心初始化
		h = G1.newElement().getImmutable();
		Y1 = Zr.newElement().getImmutable();
		Y2 = Zr.newElement().getImmutable();
		Y = Zr.newElement();
		v = G1.newElement().getImmutable();
		u = G1.newElement();

		Y = Y1.div(Y1);
		u = v.powZn(Y);

		if ((u.powZn(Y1)).isEqual(v.powZn(Y2))) {
			System.out.println("签名有效");
		} else {
			System.out.println("签名无效");
		}
	}
}
