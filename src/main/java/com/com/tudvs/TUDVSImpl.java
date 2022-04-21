package com.com.tudvs;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import org.apache.log4j.Logger;

/**
 * @author admin
 * @create 2022-04-02 13:07
 * @
 */
public class TUDVSImpl {
    private static org.apache.log4j.Logger logger = Logger.getLogger(TUDVSImpl.class);
    public static void main(String[] args) {
        /**
         * 系统初始化阶段
         */
        int rBits = 160;
        int qBits = 512;
        long begin, end;
        System.out.println("System Initialization---------------------------");
        System.out.println("Generating: group、bilinear mapping、other parameters");
        //生成一个椭圆曲线生成器Apg
        TypeACurveGenerator Apg = new TypeACurveGenerator(rBits, qBits);
        //生成配对参数
        PairingParameters typeAParams = Apg.generate();
        //初始化一个Pairing实例
        Pairing pairing = PairingFactory.getPairing(typeAParams);
        System.out.println("Generating Pairing :" + pairing);

        //群G1、G2、Zp
        Field G1 = pairing.getG1();
        Field G2 = pairing.getG2();
        Field Zp = pairing.getZr();
        System.out.println("Generating Group G1 :" + G1);
        System.out.println("Generating Group G2 :" + G2);
        System.out.println("Generating Zp :" + Zp);

        //大素数
        Element p = G1.newRandomElement().getImmutable();
        System.out.println("Generating prime p: ");
        System.out.println("p = " + p);
        //生成元P
        Element P = G1.newRandomElement().getImmutable();
        System.out.println("Generating element P: ");
        System.out.println("P = " + P);
        //主私钥
        Element s = Zp.newRandomElement().getImmutable();
        System.out.println("Generating system master private key s: ");
        System.out.println("s = " + s);
        //追溯秘钥
        Element T = s;
        System.out.println("Generating tracing key T: ");
        System.out.println("T = " + T);
        //系统主公钥P_pub、P_pub'
        Element P_pub = P.mulZn(s);
        Element P_pub1 = P_pub.mulZn(s);
        System.out.println("Generating system master public key pk: ");
        System.out.println("P_pub = " + P_pub);
        System.out.println("P_pub1 = " + P_pub1);
        System.out.println();
        System.out.println();




        /**
         * 密钥生成阶段，主要生成策略管理员的公钥和私钥，服务器管理员的公钥和私钥
         */
        System.out.println("Key Generation ---------------------------");
        String ID_s = "Policy Administrator ID";
        byte[] ID_s_m = ID_s.getBytes();
        //哈希运算  map-to-point
        //策略管理员的公钥和私钥
        Element Q_s = pairing.getG1().newRandomElement().setFromHash(ID_s_m, 0, ID_s_m.length).getImmutable();
        Element Sk_s = Q_s.mulZn(s);
        System.out.println("Generating Policy Administrator public key: ");
        System.out.println("Q_s = " + Q_s);
        System.out.println("Generating Policy Administrator private key: ");
        System.out.println("Sk_s = " + Sk_s);
        String ID_v = "Server Administrator ID";
        byte[] ID_v_m = ID_v.getBytes();
        //服务器管理员的公钥和私钥
        Element Q_v = pairing.getG1().newRandomElement().setFromHash(ID_v_m, 0, ID_v_m.length).getImmutable();
        Element Sk_v = Q_v.mulZn(s);
        System.out.println("Generating Server Administrator public key: ");
        System.out.println("Q_v = " + Q_v);
        System.out.println("Generating Server Administrator private key: ");
        System.out.println("Sk_v = " + Sk_v);
        System.out.println();
        System.out.println();


        /**
         * 签名阶段
         */
        System.out.println("Signing ---------------------------");
        String message = "message：ID_sh, lifetime, ticket, IP, access policy, Target service";
        byte[] messageBytes = message.getBytes();
        Element r = Zp.newRandomElement().getImmutable();
        Element U = Q_s.mulZn(r);
        Element h_1 = pairing.getZr().newRandomElement().setFromHash(messageBytes, 0, messageBytes.length).getImmutable();
        Element C = Sk_s.mulZn(h_1);
        Element V = Sk_s.mulZn(h_1.add(r));
        //Ticket 结果包括(U,h_1,C,V)------->发送给策略管理员
        System.out.println("Generating sign (Ticket) = (m, U, V, C): ");
        System.out.println("m = " + message);
        System.out.println("U = " + U);
        System.out.println("C = " + C);
        System.out.println("V = " + V);
        System.out.println();
        System.out.println();



        /**
         * 公开验证阶段
         */
        System.out.println("Public Verification---------------------------");
        Element PV_left1 = pairing.pairing(C,P);
        System.out.println(PV_left1);
        Element PV_right1 = pairing.pairing(Q_s.mulZn(h_1),P_pub);
        System.out.println(PV_right1);
        OutUtil.println("Verify signature1: " + PV_left1.equals(PV_right1));

        Element PV_left2 = pairing.pairing(V,P);
        System.out.println(PV_left2);
        Element param1 = Q_s.mulZn(h_1);
        Element PV_right2 = pairing.pairing(U.add(param1),P_pub);
        System.out.println(PV_right2);
        OutUtil.println("Verify signature2: " + PV_left2.equals(PV_right2));
        if(PV_left1.equals(PV_right1) && PV_left2.equals(PV_right2)){
            OutUtil.println("Signature verification succeeded");
        }else {
            OutUtil.println("Signature verification failed");
        }
        System.out.println();
        System.out.println();



        /**
         * 指定阶段
         */
        System.out.println("Designated ---------------------------");
        Element sigma = pairing.pairing(V,Q_v);
        Element t = Zp.newRandomElement().getImmutable();
        Element P1 = P.mulZn(t);
        Element a_v = Zp.newRandomElement().getImmutable();
        Element R_v = P.mulZn(a_v);
        Element R_v_ = P_pub.mulZn(a_v);
        Element a_s = Zp.newRandomElement().getImmutable();

        Element R_s = P.mulZn(a_s);
        Element P2 = P_pub.mulZn(t);
        //有问题
        Element R_s_ = P2.mulZn(a_s).sub(Q_v.mulZn(h_1));
        Element M = R_s_.add(R_v_);
        Element param2 = a_v.add(a_s.mulZn(t));
        Element S = C.add(P_pub1.mulZn(param2));
        // Ticket2 = (m,U,sigma,S,M,R_s,R_v,P1);
        System.out.println("Generating designated tikcet2 = (m, U, sigma, S, M, R_s, R_v, P1) ");
        System.out.println("sigma = " + sigma);
        System.out.println("M = " + M);
        System.out.println("S = " + S);
        System.out.println("R_s = " + R_s);
        System.out.println("R_v = " + R_v);
        System.out.println("P1 = " + P1);
        System.out.println();
        System.out.println();


        /**
         * 指定验证阶段
         */
        System.out.println("Designated Verification ---------------------------");
        Element N1 = Q_s.mulZn(h_1);
        Element N2 = Q_v.mulZn(h_1);
        Element N = N1.add(N2);
        System.out.println(sigma);
        Element DV_left1 = pairing.pairing(U.add(N1), Sk_v);
        System.out.println(DV_left1);
        OutUtil.println("Verify signature1: " + DV_left1.equals(sigma));

        Element DV_left2 = pairing.pairing(P, S);
        System.out.println(DV_left2);
        Element param3 = M.add(N);
        Element DV_right2 = pairing.pairing(P_pub, param3);
        System.out.println(DV_right2);
        OutUtil.println("Verify signature2: " + DV_left2.equals(DV_right2));
        if(DV_left1.equals(sigma) && DV_left2.equals(DV_right2)){
            OutUtil.println("Designated Signature verification succeeded");
        }else{
            OutUtil.println("Designated Signature verification failed");
        }
        System.out.println();
        System.out.println();


        /**
         * 追溯阶段
         */
        System.out.println("Tracing---------------------------");
        Element Trace_left1 = pairing.pairing(P_pub,R_v);
        Element Trace_left2 = pairing.pairing(P1.mulZn(T),R_s);

        Element Trace_left3 = pairing.pairing(P_pub,R_v.add(R_s.mulZn(t)));
        System.out.println(Trace_left3);
        Element Trace_left = Trace_left1.mulZn(Trace_left2);
        Element param4 = M.add(Q_v.mulZn(h_1));
        Element Trace_right = pairing.pairing(P,param4);
        System.out.println(Trace_right);
        OutUtil.println("Tracing: " + Trace_left3.equals(Trace_right));
        if(Trace_left3.equals(Trace_right)){
            OutUtil.println("Tracing succeeded");
        }else{
            OutUtil.println("Tracing failed");
        }
        System.out.println("===============================OVER===================================");
    }

}
