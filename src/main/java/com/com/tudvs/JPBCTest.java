package com.com.tudvs;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.util.logging.Logger;

/**
 * @author admin
 * @create 2022-04-02 12:05
 * @
 */
public class JPBCTest {

    public static void main(String[] args) {
        //初始化pairing的两种方式
        //方式一：通过文件读取产生，将JPBC库中的Type A曲线对应的参数文件a.properties直接存放在该project下
        //jpbc库中的椭圆曲线参数文件存放在params文件夹中
         // Pairing pairing = PairingFactory.getPairing("a.properties");
        //方式二：直接通过代码产生
        int rBits = 160;
        int qBits = 512;
        //生成一个椭圆曲线生成器Apg
        TypeACurveGenerator Apg = new TypeACurveGenerator(rBits, qBits);
        //生成配对参数
        PairingParameters typeAParams = Apg.generate();
        //初始化一个Pairing实例
        Pairing pairing = PairingFactory.getPairing(typeAParams);


        Field G1 = pairing.getG1();
        Field GT = pairing.getGT();
        Field Zr = pairing.getZr();

        Element g = G1.newRandomElement().getImmutable();
        Element s = Zr.newRandomElement().getImmutable();
        //要签名的消息
        String message = "welcome";
        byte[] m = message.getBytes();
        //生成H(m)
        Element hm = pairing.getG1().newRandomElement().setFromHash(m, 0, m.length).getImmutable();
        //公钥为pk=g^s和g,私钥为s
        //pk=g^s
        Element pk = g.powZn(s).getImmutable();
        //签名为H(m)^s
        Element sigma = hm.powZn(s).getImmutable();
        //验证：e(g,H(M)^s)==e(g^s,H(m))
        Element egshm = pairing.pairing(g, sigma);
        Element esghm = pairing.pairing(pk, hm);
        System.out.println(egshm.isEqual(esghm));
    }
}
