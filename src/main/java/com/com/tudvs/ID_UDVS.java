package com.com.tudvs;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

/**
 * @author admin
 * @create 2022-04-02 14:41
 * @
 */
public class ID_UDVS {
    //张方国——基于身份的UDVS方案实现代码
    public static void main(String[] args) {
        int rBits = 160;
        int qBits = 512;
        //生成一个椭圆曲线生成器Apg
        TypeACurveGenerator Apg = new TypeACurveGenerator(rBits, qBits);
        //生成配对参数
        PairingParameters typeAParams = Apg.generate();
        //初始化一个Pairing实例
        Pairing pairing = PairingFactory.getPairing(typeAParams);


        Field G1 = pairing.getG1();
        Field G2 = pairing.getG2();
        Field Zp = pairing.getZr();

        //大素数
        Element p = G1.newRandomElement().getImmutable();
        //生成元P
        Element P = G1.newRandomElement().getImmutable();
        //主私钥
        Element s = Zp.newRandomElement().getImmutable();
        //追溯秘钥
        Element T = s;
        //系统公钥
        Element P_pub = P.mulZn(s);
        String ID_s = "client_id_s";
        byte[] ID_m = ID_s.getBytes();
        //哈希运算  T3 = pairing.getG1().newElement().setFromHash(byt, 0, byt.length);// map-to-point
        Element Q_ID = pairing.getG1().newRandomElement().setFromHash(ID_m, 0, ID_m.length).getImmutable();
        Element S_ID = Q_ID.mulZn(s);

        //signing
        Element r = Zp.newRandomElement().getImmutable();
        Element U = Q_ID.mulZn(r);
        String mess = "message";
        byte[] m = mess.getBytes();
        Element h = pairing.getZr().newRandomElement().setFromHash(m, 0, m.length).getImmutable();
        Element V = S_ID.mulZn(h.add(r));

        Element left = pairing.pairing(V, P);
        System.out.println(left);
        //can = H1(U+m)QID
        Element can = Q_ID.mulZn(h);
        Element right = pairing.pairing(U.add(can), P_pub);
        System.out.println(right);
        //公开验证的正确性
        System.out.println(left.equals(right));

        //指定
        //生成指定验证者的公钥
        String ID_v = "client_id_v";
        byte[] ID_v_m = ID_v.getBytes();
        //哈希运算  T3 = pairing.getG1().newElement().setFromHash(byt, 0, byt.length);// map-to-point
        Element Q_ID_v = pairing.getG1().newRandomElement().setFromHash(ID_v_m, 0, ID_v_m.length).getImmutable();
        Element S_ID_v = Q_ID_v.mulZn(s);

        Element deta = pairing.pairing(V, Q_ID_v);
        Element can_v = Q_ID.mulZn(h);
        Element left_v = pairing.pairing(U.add(can_v), S_ID_v);
        System.out.println(deta);
        System.out.println(left_v);
        System.out.println(deta.equals(left_v));
    }
}
