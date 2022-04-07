package com.com.jpbc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class First {

	public static void main(String[] args) {

		int rBits = 160;
		int qBits = 512;
		Element P, Y, M, W, x, T1, T2;
		Field G1, GT, Zr;
		Pairing pairing;
		long begin, end;
		// JPBC Type A pairing generator...
		// ParametersGenerator pbcPg = new PBCTypeACurveGenerator(rBits, qBits);

		pairing = PairingFactory.getPairing("a.properties");//
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		// TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);
		// PairingParameters typeAParams = pg.generate();
		// pairing = PairingFactory.getPairing(typeAParams);

		Zr = pairing.getZr();
		G1 = pairing.getG1();
		GT = pairing.getGT();

		// 初始化相关参数
		P = G1.newRandomElement().getImmutable();// 生成元
		Y = G1.newElement();
		M = G1.newElement();
		W = G1.newElement();

		T1 = G1.newElement();
		T2 = G1.newElement();

		if (!pairing.isSymmetric()) {
			throw new RuntimeException("密钥不对称!");
		}

		System.out.println("BLS 机制\n密钥生成阶段");
		System.out
				.println("-------------------------------------------------------");
		begin = System.currentTimeMillis();
		x = Zr.newRandomElement().getImmutable();// 生成私钥
		System.out.println(x);
		Y = P.mulZn(x);
		end = System.currentTimeMillis();
		System.out.println("G1的生成元：");
		System.out.println("P = " + P);
		System.out.println("私钥：");
		System.out.println("x = " + x);
		System.out.println("公钥：");
		System.out.println("Y = " + Y);
		System.out.println("耗时:" + (end - begin) + "ms");

		System.out.println("签名阶段");
		System.out
				.println("-------------------------------------------------------");
		String message = "message";
		byte[] byt = message.getBytes(); // 先变成比特

		begin = System.currentTimeMillis();
		System.out.println("消息：" + message);
		M = pairing.getG1().newElement().setFromHash(byt, 0, byt.length);
		W = M.mulZn(x);// 计算W = xM
		end = System.currentTimeMillis();
		System.out.println("H(m) = " + M);
		System.out.println("消息的签名是：");
		System.out.println("W = " + W);
		System.out.println("耗时:" + (end - begin) + "ms");

		System.out.println("验证阶段");
		System.out
				.println("-------------------------------------------------------");
		begin = System.currentTimeMillis();
		T1 = pairing.pairing(P, W).getImmutable();

		M = pairing.getG1().newElement().setFromHash(byt, 0, byt.length);// 模拟接收方求取消息hash

		T2 = pairing.pairing(Y, M).getImmutable();

		if (T1.isEqual(T2)) {
			System.out.println("签名有效");
		} else {
			System.out.println("签名无效");
		}

		end = System.currentTimeMillis();
		System.out.println("e(P, W) =  " + T1);
		System.out.println("e(Y, H(m)) =  " + T2);

		System.out.println("耗时:" + (end - begin) + "ms");

		byt = W.toBytes();
		System.out.println("签名的字节长度：" + byt.length);

		Element G_1 = pairing.getG1().newRandomElement().getImmutable();
		Element G_2 = pairing.getG2().newRandomElement().getImmutable();
		Element Z = pairing.getZr().newRandomElement().getImmutable();
		Element G_T = pairing.getGT().newRandomElement().getImmutable();

		Element G_1_p = pairing.getG1().newRandomElement().getImmutable();
		Element G_2_p = pairing.getG2().newRandomElement().getImmutable();
		Element Z_p = pairing.getZr().newRandomElement().getImmutable();
		Element G_T_p = pairing.getGT().newRandomElement().getImmutable();

		// G_1的相关运算
		// G_1 multiply G_1
		Element G_1_m_G_1 = G_1.mul(G_1_p);
		// G_1 power Z
		Element G_1_e_Z = G_1.powZn(Z);

		// G_2的相关运算
		// G_2 multiply G_2
		Element G_2_m_G_2 = G_2.mul(G_2_p);
		// G_2 power Z
		Element G_2_e_Z = G_2.powZn(Z);

		// G_T的相关运算
		// G_T multiply G_T
		Element G_T_m_G_T = G_T.mul(G_T_p);
		// G_T power Z
		Element G_T_e_Z = G_T.powZn(Z);

		// Z的相关运算
		// Z add Z
		Element Z_a_Z = Z.add(Z_p);
		// Z multiply Z
		Element Z_m_Z = Z.mul(Z_p);

		// Pairing运算
		Element G_p_G = pairing.pairing(G_1, G_2);

	}

}
