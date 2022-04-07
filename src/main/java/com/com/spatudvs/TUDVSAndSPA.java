package com.com.spatudvs;
import com.com.spatudvs.SPAPacket;
import com.com.tudvs.OutUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.util.HashMap;
import java.util.Map;


/**
 * @author admin
 * @create 2021-12-27 13:22
 * @ 描述：简单实现TUDVS和SPA的结合
 */
public class TUDVSAndSPA {
    //模拟数据库中存放的用户消息内容，比如：账号，密码，设备标识，IP地址等。
    static final Map<String, Object> map = new HashMap<>();
    static {
        map.put("username","Machunliang");
        map.put("password","123456");
        map.put("Client_id","Client_SPAUser");
        map.put("IsPrivilege","Yes");
    }
    public static void main(String[] args) throws Exception {
        /**
         * 1. 模拟用户生成单包信息SPA
         */
        OutUtil.printlnBlue("-----------------------------1. User request and generate SPA-----------------------------");
        SPAPacket spaPacket = SPAPacket.GenerSpa();
        System.out.println("SPA : ");
        System.out.println(spaPacket);
        System.out.println();


        /**
         * 2. 模拟认证中心认证单包信息
         */
        OutUtil.printlnBlue("-----------------------------2. Authentication center verify-----------------------------");
        String username = spaPacket.getUsername();
        //验证用户的身份
        if(username.equals(map.get("username")) && "Client_SPAUser".equals(map.get("Client_id")) && "Yes".equals(map.get("IsPrivilege"))){
            OutUtil.println("legitimate users :  Generate Ticket" );
        }else{
            System.out.println("Illegal user identity: access denied");
        }


        /**
         * 系统初始化阶段
         */
        int rBits = 160;
        int qBits = 512;
        long begin, end;
        //生成一个椭圆曲线生成器Apg
        TypeACurveGenerator Apg = new TypeACurveGenerator(rBits, qBits);
        //生成配对参数
        PairingParameters typeAParams = Apg.generate();
        //初始化一个Pairing实例
        Pairing pairing = PairingFactory.getPairing(typeAParams);

        //群G1、G2、Zp
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
        //系统主公钥P_pub、P_pub1
        Element P_pub = P.mulZn(s);
        Element P_pub1 = P_pub.mulZn(s);


        /**
         * 密钥生成阶段，主要生成策略管理员的公钥和私钥，服务器管理员的公钥和私钥
         */
        String ID_s = "Policy Administrator ID";
        byte[] ID_s_m = ID_s.getBytes();
        //策略管理员的公钥和私钥
        Element Q_s = pairing.getG1().newRandomElement().setFromHash(ID_s_m, 0, ID_s_m.length).getImmutable();
        Element Sk_s = Q_s.mulZn(s);
        String ID_v = "Server Administrator ID";
        byte[] ID_v_m = ID_v.getBytes();
        //服务器管理员的公钥和私钥
        Element Q_v = pairing.getG1().newRandomElement().setFromHash(ID_v_m, 0, ID_v_m.length).getImmutable();
        Element Sk_v = Q_v.mulZn(s);


        //生成票据信息
        String ticket = "ticket || id_user || lifetime || timestamp || target resources || access time ...";
        byte[] messageBytes = ticket.getBytes();
        Element r = Zp.newRandomElement().getImmutable();
        Element U = Q_s.mulZn(r);
        Element h_1 = pairing.getZr().newRandomElement().setFromHash(messageBytes, 0, messageBytes.length).getImmutable();
        Element C = Sk_s.mulZn(h_1);
        Element V = Sk_s.mulZn(h_1.add(r));
        //Ticket 结果包括(U,h_1,C,V)------->发送给策略管理员
        System.out.println("Generating Ticket = ticket || id_user || lifetime || timestamp || target resources || access time ...");
        System.out.println("Generating Signature :");
        System.out.println("U =" + U);
        System.out.println("V =" + V);
        System.out.println("C =" + C);
        System.out.println("SDP Open  port 22.");
        System.out.println();


        /**
         * 3. 用户将原始票据转化为指定票据
         */
        OutUtil.printlnBlue("-----------------------------3. User Designated Ticket-----------------------------");
        System.out.println("verify original ticket");
        Element PV_left1 = pairing.pairing(C,P);
        System.out.println(PV_left1);
        Element PV_right1 = pairing.pairing(Q_s.mulZn(h_1),P_pub);
        System.out.println(PV_right1);
        OutUtil.println("Verify ticket1: " + PV_left1.equals(PV_right1));

        Element PV_left2 = pairing.pairing(V,P);
        System.out.println(PV_left2);
        Element param1 = Q_s.mulZn(h_1);
        Element PV_right2 = pairing.pairing(U.add(param1),P_pub);
        System.out.println(PV_right2);
        OutUtil.println("Verify ticket2: " + PV_left2.equals(PV_right2));
        if(PV_left1.equals(PV_right1) && PV_left2.equals(PV_right2)){
            OutUtil.println("Ticket verification succeeded");
        }else {
            OutUtil.println("Ticket verification failed");
        }
        //指定阶段
        System.out.println("User designated ");
        Element sigma = pairing.pairing(V,Q_v);
        Element t = Zp.newRandomElement().getImmutable();
        Element P1 = P.mulZn(t);
        Element a_v = Zp.newRandomElement().getImmutable();
        Element R_v = P.mulZn(a_v);
        Element R_v_ = P_pub.mulZn(a_v);
        Element a_s = Zp.newRandomElement().getImmutable();

        Element R_s = P.mulZn(a_s);
        Element P2 = P_pub.mulZn(t);
        Element R_s_ = P2.mulZn(a_s).sub(Q_v.mulZn(h_1));
        Element M = R_s_.add(R_v_);
        Element param2 = a_v.add(a_s.mulZn(t));
        Element S = C.add(P_pub1.mulZn(param2));
        // Ticket2 = (m,U,sigma,S,M,R_s,R_v,P1);

        System.out.println("Generating designated ticket signature :");
        System.out.println("sigma = " + sigma);
        System.out.println("M = " + M);
        System.out.println("S = " + S);
        System.out.println("R_s = " + R_s);
        System.out.println("R_v = " + R_v);
        System.out.println("P1 = " + P1);
        System.out.println();


        /**
         * 4. 数据管理者验证
         */
        OutUtil.printlnBlue("-----------------------------4. Data manager verify-----------------------------");
        Element N1 = Q_s.mulZn(h_1);
        Element N2 = Q_v.mulZn(h_1);
        Element N = N1.add(N2);
        System.out.println(sigma);
        Element DV_left1 = pairing.pairing(U.add(N1), Sk_v);
        System.out.println(DV_left1);
        OutUtil.println("Verify designated ticket 1:  " + DV_left1.equals(sigma));

        Element DV_left2 = pairing.pairing(P, S);
        System.out.println(DV_left2);
        Element param3 = M.add(N);
        Element DV_right2 = pairing.pairing(P_pub, param3);
        System.out.println(DV_right2);
        OutUtil.println("Verify designated ticket 2: " + DV_left2.equals(DV_right2));
        if(DV_left1.equals(sigma) && DV_left2.equals(DV_right2)){
            OutUtil.println("Designated Ticket verification succeeded");
        }else{
            OutUtil.println("Designated Ticket verification failed");
        }
        System.out.println();


        /**
         * 5. 向追溯中心发起请求
         */
        OutUtil.printlnBlue("-----------------------------5. Data manager initiate arbitration request-----------------------------");
        System.out.println("Send the designated ticket to tracing center");
        System.out.println();


        /**
         * 6. 追溯中心追溯
         */
        OutUtil.printlnBlue("-----------------------------6. Tracing-----------------------------");

        Element Trace_left3 = pairing.pairing(P_pub,R_v.add(R_s.mulZn(t)));
        System.out.println(Trace_left3);
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
