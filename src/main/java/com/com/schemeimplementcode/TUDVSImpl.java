package com.com.schemeimplementcode;

import com.com.tudvs.OutUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;



/**
 * @author admin
 * @create 2022-04-15 12:15
 * @
 */
public class TUDVSImpl {

    static Pairing pairing;
    static Field G1;
    static Field G2;
    static Field Zp;
    static Element p;
    static Element P;
    static Element s;
    static Element T;
    static Element P_pub;
    static Element P_pub1;
    static Element Q_s;
    static Element Sk_s;
    static Element Q_v;
    static Element Sk_v;

    public static void main(String[] args) {
        //1. 系统建立
        setup();

        //2. 密钥生成
        keyGeneration();

        //3. 签名
        signing();

        //4. 公开验证
        publicVerification();

        //5. 指定
        designation();

        //6. 指定验证
        designatedVerification();

        //7. 追溯
        tracing();
    }

    /**
     * System Setup
     */
    public static void setup(){
        int rBits = 160;
        int qBits = 512;
        System.out.println("---------------------------System Setup---------------------------");
        System.out.println("Generating: group, bilinear mapping, other parameters");
        TypeACurveGenerator Apg = new TypeACurveGenerator(rBits, qBits);
        PairingParameters typeAParams = Apg.generate();
        pairing = PairingFactory.getPairing(typeAParams);
        System.out.println("Generating bilinear pairing :" + pairing);
        G1 = pairing.getG1();
        G2 = pairing.getGT();
        Zp = pairing.getZr();
        System.out.println("Generating Group G1 :" + G1);
        System.out.println("Generating Group G2 :" + G2);
        System.out.println("Generating Zp :" + Zp);
        p = G1.newRandomElement().getImmutable();
        System.out.println("Generating prime p: ");
        System.out.println("p = " + p);
        P = G1.newRandomElement().getImmutable();
        System.out.println("Generating element P: ");
        System.out.println("P = " + P);
        s = Zp.newRandomElement().getImmutable();
        System.out.println("Generating system master private key s: ");
        System.out.println("s = " + s);
        T = s;
        System.out.println("Generating tracing key T: ");
        System.out.println("T = " + T);
        P_pub = P.mulZn(s);
        P_pub1 = P_pub.mulZn(s);
        System.out.println("Generating system master public key pk: ");
        System.out.println("P_pub = " + P_pub);
        System.out.println("P_pub1 = " + P_pub1);
        System.out.println();
        System.out.println();
    }



    /**
     * Key Generation
     */
    public static void keyGeneration(){
        System.out.println("---------------------------Key Generation---------------------------");
        String ID_s = "Policy Administrator ID";
        byte[] ID_s_m = ID_s.getBytes();
        Q_s = pairing.getG1().newRandomElement().setFromHash(ID_s_m, 0, ID_s_m.length).getImmutable();
        Sk_s = Q_s.mulZn(s);
        System.out.println("Generating Policy Administrator public key: ");
        System.out.println("Q_s = " + Q_s);
        System.out.println("Generating Policy Administrator private key: ");
        System.out.println("Sk_s = " + Sk_s);
        String ID_v = "Server Administrator ID";
        byte[] ID_v_m = ID_v.getBytes();
        Q_v = pairing.getG1().newRandomElement().setFromHash(ID_v_m, 0, ID_v_m.length).getImmutable();
        Sk_v = Q_v.mulZn(s);
        System.out.println("Generating Server Administrator public key: ");
        System.out.println("Q_v = " + Q_v);
        System.out.println("Generating Server Administrator private key: ");
        System.out.println("Sk_v = " + Sk_v);
        System.out.println();
        System.out.println();
    }

    /**
     * Signing
     */
    static Element  r;
    static Element U;
    static Element h_1;
    static Element C;
    static Element V;
    public static void signing(){
        System.out.println("---------------------------Signing---------------------------");
        String message = "message：ID_sh, lifetime, ticket, IP, access policy, Target service";
        byte[] messageBytes = message.getBytes();
        r = Zp.newRandomElement().getImmutable();
        U = Q_s.mulZn(r);
        h_1 = pairing.getZr().newRandomElement().setFromHash(messageBytes, 0, messageBytes.length).getImmutable();
        C = Sk_s.mulZn(h_1);
        V = Sk_s.mulZn(h_1.add(r));
        //Ticket =(U,h_1,C,V)------->send to server
        System.out.println("Generating sign (Ticket) = (m, U, V, C): ");
        System.out.println("m = " + message);
        System.out.println("U = " + U);
        System.out.println("C = " + C);
        System.out.println("V = " + V);
        System.out.println();
        System.out.println();
    }


    /**
     * Public Verification
     */
    static Element PV_left1;
    static Element PV_right1;
    static Element PV_left2;
    static Element param1;
    static Element PV_right2;
    public static void publicVerification(){
        System.out.println("---------------------------Public Verification---------------------------");
        PV_left1 = pairing.pairing(C,P);
        System.out.println(PV_left1);
        PV_right1 = pairing.pairing(Q_s.mulZn(h_1),P_pub);
        System.out.println(PV_right1);
        OutUtil.println("Verify signature1: " + PV_left1.equals(PV_right1));
        PV_left2 = pairing.pairing(V,P);
        System.out.println(PV_left2);
        param1 = Q_s.mulZn(h_1);
        PV_right2 = pairing.pairing(U.add(param1),P_pub);
        System.out.println(PV_right2);
        OutUtil.println("Verify signature2: " + PV_left2.equals(PV_right2));
        if(PV_left1.equals(PV_right1) && PV_left2.equals(PV_right2)){
            OutUtil.println("Signature verification succeeded");
        }else {
            OutUtil.println("Signature verification failed");
        }
        System.out.println();
        System.out.println();
    }



    /**
     * Designation
     */
    static Element sigma;
    static Element t;
    static Element P1;
    static Element a_v;
    static Element R_v;
    static Element R_v_;
    static Element a_s;

    static Element R_s;
    static Element P2;
    static Element R_s_;
    static Element M;
    static Element param2;
    static Element S;
    public static void designation(){
        System.out.println("---------------------------Designation---------------------------");
        sigma = pairing.pairing(V,Q_v);
        t = Zp.newRandomElement().getImmutable();
        P1 = P.mulZn(t);
        a_v = Zp.newRandomElement().getImmutable();
        R_v = P.mulZn(a_v);
        R_v_ = P_pub.mulZn(a_v);
        a_s = Zp.newRandomElement().getImmutable();

        R_s = P.mulZn(a_s);
        P2 = P_pub.mulZn(t);
        R_s_ = P2.mulZn(a_s).sub(Q_v.mulZn(h_1));
        M = R_s_.add(R_v_);
        param2 = a_v.add(a_s.mulZn(t));
        S = C.add(P_pub1.mulZn(param2));
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
    }


    /**
     * Designated Verification
     */
    static Element N;
    static Element DV_left1;
    static Element DV_left2;
    static Element param3;
    static Element DV_right2;
    public static void designatedVerification(){
        System.out.println("---------------------------Designated Verification---------------------------");
        Element N1 = Q_s.mulZn(h_1);
        Element N2 = Q_v.mulZn(h_1);
        N = N1.add(N2);
        System.out.println(sigma);
        DV_left1 = pairing.pairing(U.add(N1), Sk_v);
        System.out.println(DV_left1);
        OutUtil.println("Verify signature1: " + DV_left1.equals(sigma));

        DV_left2 = pairing.pairing(P, S);
        System.out.println(DV_left2);
        param3 = M.add(N);
        DV_right2 = pairing.pairing(P_pub, param3);
        System.out.println(DV_right2);
        OutUtil.println("Verify signature2: " + DV_left2.equals(DV_right2));
        if(DV_left1.equals(sigma) && DV_left2.equals(DV_right2)){
            OutUtil.println("Designated Signature verification succeeded");
        }else{
            OutUtil.println("Designated Signature verification failed");
        }
        System.out.println();
        System.out.println();
    }



    /**
     * Tracing
     */
    static Element Trace_left1;
    static Element Trace_left2;
    static Element Trace_left3;
    static Element Trace_left;
    static Element param4;
    static Element Trace_right;
    public static void tracing(){
        System.out.println("---------------------------Tracing---------------------------");
        Trace_left1 = pairing.pairing(P_pub,R_v);
        Trace_left2 = pairing.pairing(P1.mulZn(T),R_s);
        Trace_left3 = pairing.pairing(P_pub,R_v.add(R_s.mulZn(t)));
        System.out.println(Trace_left3);
        Trace_left = Trace_left1.mulZn(Trace_left2);
        param4 = M.add(Q_v.mulZn(h_1));
        Trace_right = pairing.pairing(P,param4);
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
