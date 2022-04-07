package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Setup {

	public static boolean setup(int t, int n, Pairing pairing, Element g) {
		System.out.println("g=" + g);
		String[] ID = new String[n];// 生产用户ID
		for (int i = 0; i < ID.length; i++) {
			ID[i] = String.valueOf(i + 1);
			System.out.println("ID[" + i + "]=" + ID[i]);
		}

		Element c[][] = new Element[n][t + 1];// 生产每个中心的随机函数
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[i].length; j++) {
				c[i][j] = Random_element.randomZr(pairing);
				System.out.println("c[" + i + "][" + j + "]" + c[i][j]);
			}
		}

		Element C[][] = new Element[n][t + 1];// 计算C_ik
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[i].length; j++) {
				C[i][j] = g.powZn(c[i][j]);
				System.out.println("C[" + i + "][" + j + "]" + C[i][j]);
			}
		}
		Element[] H1_ID = new Element[n];// 计算H1_ID
		for (int i = 0; i < H1_ID.length; i++) {
			byte[] byt = ID[i].getBytes();
			H1_ID[i] = pairing.getZr().newElement()
					.setFromHash(byt, 0, byt.length);
			System.out.println("H1_ID[" + i + "]=" + H1_ID[i]);
		}
		for (int i = 0; i < H1_ID.length; i++) {
			System.out.println("H1_ID[" + i + "]=" + H1_ID[i]);
		}
		Map<String, Element[][]> map = computeH_i(c, H1_ID);
		Element[][] z_ij = map.get("z");
		Element[][] t_ij = map.get("t");

		boolean ver_1 = verify_1(t_ij, C, H1_ID, g, z_ij);
		return ver_1;

	}

	public static boolean verify_1(Element[][] t_ij, Element C[][],
			Element[] H1_ID, Element g, Element[][] z_ij) {
		for (int i = 0; i < t_ij.length; i++) {
			for (int j = 0; j < t_ij[i].length; j++) {
				Element T1 = g.powZn(t_ij[i][j]);
				System.out.println("T1:" + T1);
				Element T2 = C[i][0];
				System.out.println("T2:" + T2);
				for (int k = 0; k < z_ij[i].length; k++) {
					System.out.println("z_ij[" + j + "][" + k + "]="
							+ H1_ID[j].pow(BigInteger.valueOf(k + 1)));

					T2 = T2.mul(C[i][k].powZn(H1_ID[j].pow(BigInteger
							.valueOf(k + 1))));
					// System.out.println("z_ij[" + j + "][" + k + "]="
					// + z_ij[j][k]);
					// T2 = T2.mul(C[i][k].powZn(z_ij[j][k]));

					System.out.println("T2:" + T2);
				}

				if (!T1.isEqual(T2)) {
					return false;
				}
			}
		}
		return true;

	}

	public static Map<String, Element[][]> computeH_i(Element c[][],
			Element[] H1_ID) {// 计算t_ij
		Map<String, Element[][]> map = new HashMap<String, Element[][]>();
		System.out
				.println("======================================================");
		for (int i = 0; i < H1_ID.length; i++) {
			System.out.println("H1_ID[" + i + "]=" + H1_ID[i]);
		}
		int n = H1_ID.length;
		int t = c[0].length - 1;
		Element tempt;
		Element[][] t_ij = new Element[n][n];
		Element[][] z_ij = new Element[n][t];
		Element[] Z = new Element[10];
		System.out.println(z_ij.length);
		System.out.println(z_ij[0].length);
		for (int i = 0; i < z_ij.length; i++) {
			for (int j = 0; j < z_ij[i].length; j++) {
				z_ij[i][j] = H1_ID[i].pow(BigInteger.valueOf(j + 1));
				System.out.println("z_ij[" + i + "][" + j + "]=" + z_ij[i][j]);
				// z_ij[i][j] = pow(H1_ID[j], j + 1);
				// System.out.println(z_ij[i][j]);
			}
		}
		System.out
				.println("======================================================");
		for (int i = 0; i < z_ij.length; i++) {
			for (int j = 0; j < z_ij[i].length; j++) {
				System.out.println("z_ij[" + i + "][" + j + "]=" + z_ij[i][j]);
			}
		}
		System.out
				.println("======================================================");
		for (int i = 0; i < H1_ID.length; i++) {
			System.out.println("H1_ID[" + i + "]=" + H1_ID[i]);
		}
		map.put("z", z_ij);

		for (int i = 0; i < t_ij.length; i++) {
			for (int j = 0; j < t_ij[i].length; j++) {
				Element H_sum = c[i][0];
				for (int k = 0; k < z_ij[i].length; k++) {

					H_sum = H_sum.add(c[i][k + 1].mulZn(z_ij[j][k]));
					// H_sum = H_sum.add(c[i][k + 1].mul(z_ij[j][k]));
				}
				t_ij[i][j] = H_sum;
				System.out.println("t_ij[" + i + "][" + j + "]=" + H_sum);

			}
		}
		map.put("t", t_ij);
		return map;

	}

	public static Element pow_my(Element a, int n) {// a的n次方
		Element powElement = a;
		for (int i = 1; i < n; i++) {
			powElement = powElement.mul(a);
		}
		return powElement;
	}
	// public static byte[] int2Bytes(int integer) {// int转byte
	// byte[] bytes = new byte[4];
	// bytes[3] = (byte) (integer >> 24);
	// bytes[2] = (byte) (integer >> 16);
	// bytes[1] = (byte) (integer >> 8);
	// bytes[0] = (byte) integer;
	//
	// return bytes;
	// }
}
