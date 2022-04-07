package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Test1 {
	public static void main(String[] args) {
		int rBits = 160;
		int qBits = 512;
		Element P, x, T0, T1, T2, T3;
		Field G1, GT, Zr;
		Pairing pairing;
		long begin, end;
		pairing = PairingFactory.getPairing("a.properties");
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		Zr = pairing.getZr();
		G1 = pairing.getG1();
		GT = pairing.getGT();

		P = G1.newRandomElement().getImmutable();// 生成元

		T0 = G1.newElement();
		T1 = G1.newElement();
		T2 = G1.newElement();
		T3 = G1.newElement();

		x = Zr.newRandomElement().getImmutable();
		System.out.println("群的指数运算");
		begin = System.nanoTime();
		T0 = P.powZn(x);// 群的指数运算
		end = System.nanoTime();
		System.out.println("耗时:" + (end - begin) / 1000000.0 + "ms");
		System.out
				.println("-------------------------------------------------------");

		System.out.println("双线性运算");
		begin = System.nanoTime();
		T1 = pairing.pairing(P, T0).getImmutable();// 双线性运算
		end = System.nanoTime();
		System.out.println("耗时:" + (end - begin) / 1000000.0 + "ms");
		System.out
				.println("-------------------------------------------------------");

		System.out.println("群的点乘运算");
		begin = System.nanoTime();
		T2 = P.mul(T0);// 群的点乘运算
		end = System.nanoTime();
		System.out.println("耗时:" + (end - begin) / 1000000.0 + "ms");
		System.out
				.println("-------------------------------------------------------");

		System.out.println("map-to-point哈希运算");
		String message = "message";
		byte[] byt = message.getBytes(); // 先变成比特
		begin = System.nanoTime();
		T3 = pairing.getZr().newElement().setFromHash(byt, 0, byt.length);// map-to-point哈希运算
		end = System.nanoTime();
		System.out.println("耗时:" + (end - begin) / 1000000.0 + "ms");

		Element Temp = pairing.getZr().newElement().setFromHash(byt, 0, byt.length);// map-to-point哈希运算
		begin = System.nanoTime();
		Element T5 = T3.powZn(x);
		end = System.nanoTime();
		System.out.println("耗时:" + (end - begin) / 1000000.0 + "ms");
	}

}
