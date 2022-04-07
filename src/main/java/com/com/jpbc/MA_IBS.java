package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Scanner;

public class MA_IBS {
	public static void main(String[] args) {
		Pairing pairing;
		Element p1, g;
		pairing = PairingFactory.getPairing("a.properties");
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		p1 = Random_element.randomZr(pairing);

		g = Random_element.randomGT(pairing);

		if (!pairing.isSymmetric()) {
			throw new RuntimeException("密钥不对称!");
		}

		Scanner scanner = new Scanner(System.in);
		int t = scanner.nextInt(); // 输入门限（t,n）
		int n = scanner.nextInt();

		boolean ver = Setup.setup(t, n, pairing, g);
		System.out.println(ver);
	}
}
