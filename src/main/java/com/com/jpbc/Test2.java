package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Test2 {
	public static void main(String[] args) {
		Element s, Hid, skid, H1id, r, u, t, v, T1, T2, T3, g, y;
		Field G1, GT, Zr;
		long begin, end;
		Pairing pairing;
		pairing = PairingFactory.getPairing("a.properties");
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		Zr = pairing.getZr();
		G1 = pairing.getG1();
		GT = pairing.getGT();

		s = Zr.newElement().getImmutable();// 主私钥
		Hid = G1.newElement().getImmutable();// 模拟H（id）
		g = G1.newElement().getImmutable();
		y = G1.newElement();
		skid = G1.newElement();
		u = G1.newElement();
		t = Zr.newElement();
		v = G1.newElement();
		T1 = G1.newElement();
		T2 = G1.newElement();
		T3 = G1.newElement();
		r = Zr.newElement().getImmutable();// 签名随机选取的整数
		H1id = Zr.newElement().getImmutable();// 模拟H1（id）

		skid = Hid.powZn(s);// 用户私钥
		y = g.powZn(s);// 主公钥
		System.out.println("签名阶段");
		System.out
				.println("-------------------------------------------------------");
		begin = System.nanoTime();
		u = Hid.powZn(r);
		t = H1id;
		v = skid.powZn(r.add(t));
		end = System.nanoTime();
		System.out.println("耗时:" + (end - begin) / 1000000.0 + "ms");
		System.out.println("验证阶段");
		System.out
				.println("-------------------------------------------------------");
		T1 = pairing.pairing(v, g);
		T2 = pairing.pairing(u, y);
		T3 = pairing.pairing(Hid.powZn(t), y);
		if (T1.isEqual(T2.mul(T3))) {
			System.out.println("签名有效");
		} else {
			System.out.println("签名无效");
		}
		System.out.println("批量验证阶段");
		System.out
				.println("-------------------------------------------------------");
	}

}
