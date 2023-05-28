package com.competition.recommend.util;

import java.math.BigInteger;
import java.util.*;

public class THMDEM {
    private static final int pp = 32;


    public static BigInteger GenPrime(int length){
        Random r = new Random();
        return BigInteger.probablePrime(length,r);

    }

    public static BigInteger[][] UserKeyGen(int L){
        BigInteger[][] result = new BigInteger[L][6];
        for (int i=0;i<L;i++) {
            result[i][0] = GenPrime(pp);//p
            result[i][1] = GenPrime(pp);//q
            result[i][2] = GenPrime(pp);//s
            result[i][3] = result[i][0].multiply(result[i][1]);//N
            result[i][4] = result[i][0].multiply(result[i][1]).multiply(result[i][2]);//T
            result[i][5] = GenPrime(pp);//p_0
        }
        return result;
    }

    public static BigInteger[] SeverKeyGen(){
        BigInteger[] result = new BigInteger[11];
        result[0]=GenPrime(pp);
        result[1]=GenPrime(pp);
        result[2]=GenPrime(pp);
        result[3]=result[0].multiply(result[1]);
        result[4]=result[0].multiply(result[1]).multiply(result[2]);
        result[5]=GenPrime(2*pp);
        result[6]=GenPrime(2*pp);
        result[7]=GenPrime(2*pp);
        result[8]=result[5].multiply(result[6]);
        result[9]=result[5].multiply(result[6]).multiply(result[7]);
        result[10]=GenPrime(2*pp);
        return result;  //<p,q,s,N,T,p',q',s',N',T',p0> 前五个计算函数用，后六个比较算法用。
    }

    public static List<Map> Encrypt(int[] m, BigInteger p, BigInteger q, BigInteger N, BigInteger T, BigInteger p_0, String pk_ser, String pk_csp) {
        BigInteger r = GenPrime(pp);
        BigInteger r_tag = GenPrime(pp);
        int l = m.length;
        int[] s = new int[l];
        for (int i = 0; i < l; i++)
            s[i] = m[i] == 0 ? 1 : 0;
        BigInteger[] C = new BigInteger[l];
        BigInteger[] C_tag = new BigInteger[l];
        BigInteger p_ = p.modInverse(q),q_=q.modInverse(p);
        for (int i = 0; i < l; i++) {
            BigInteger k1 = GenPrime(pp);
            BigInteger k2 = GenPrime(pp);
            BigInteger k1_tag = GenPrime(pp);
            BigInteger k2_tag = GenPrime(pp);
            BigInteger t = GenPrime(pp);
            BigInteger msp_q = BigInteger.valueOf(m[i] + s[i]).add(k1.multiply(p_0)).mod(q);
            BigInteger msp_p = BigInteger.valueOf(m[i] + s[i]).add(k1.multiply(p_0)).mod(p);
            BigInteger tmp1 = p.multiply(p_).multiply(msp_q).add(q.multiply(q_).multiply(msp_p));
            C[i] = tmp1.add(k2.multiply(N)).mod(T);
            BigInteger add = t.pow(s[i]).add(k1_tag.multiply(p_0));
            BigInteger tp_q = add.mod(q);
            BigInteger tp_p = add.mod(p);
            BigInteger tmp2 = p.multiply(p_).multiply(tp_q).add(q.multiply(q_).multiply(tp_p));
            C_tag[i] = tmp2.add(k2_tag.multiply(N)).mod(T);
        }

        String C_ser = MySM2.encryptSm2(pk_ser, String.valueOf(r));
        String C_ser_tag = MySM2.encryptSm2(pk_ser, String.valueOf(r_tag));
        String C_csp = MySM2.encryptSm2(pk_csp, String.valueOf(N));
        List<Map> result = new ArrayList<>();
        Map<String, String> pkeMap = new HashMap<>();
        pkeMap.put("C_ser",C_ser);
        pkeMap.put("C_ser_tag", C_ser_tag);
        pkeMap.put("C_csp",C_csp);
        Map<String, BigInteger[]> CMap = new HashMap<>();
        CMap.put("C",C);
        CMap.put("C_tag",C_tag);

        result.add(pkeMap);
        result.add(CMap);
        return result;//<C_ser,C_ser_tag,C_csp>,<C,C_tag>
    }

    public static List<Map> KeySwitch(List<Map> C_sen, BigInteger p, BigInteger q, BigInteger N, BigInteger T, BigInteger p_0_i, String sk_ser, String sk_csp){
        String S_r_i = MySM2.decryptSm2(sk_ser, (String) C_sen.get(0).get("C_ser"));
        String S_r_i_tag = MySM2.decryptSm2(sk_ser, (String) C_sen.get(0).get("C_ser_tag"));
        String S_N_i = MySM2.decryptSm2(sk_csp, (String) C_sen.get(0).get("C_csp"));
        BigInteger r_i = new BigInteger(S_r_i);
        BigInteger r_i_tag = new BigInteger(S_r_i_tag);
        BigInteger N_i = new BigInteger(S_N_i);

        BigInteger r_i_ = r_i.modInverse(T);
        BigInteger r_i_tag_ = r_i_tag.modInverse(T);

        BigInteger[] C_i = (BigInteger[]) C_sen.get(1).get("C");
        BigInteger[] C_i_tag = (BigInteger[]) C_sen.get(1).get("C_tag");

        int l = C_i.length;
        BigInteger[] C_ser = new BigInteger[l];
        BigInteger[] C_ser_tag = new BigInteger[l];
        BigInteger p_ = p.modInverse(q),q_=q.modInverse(p);
        BigInteger r = GenPrime(pp);
        BigInteger r_tag = GenPrime(pp);
        for(int i= 0;i<l;i++){
            BigInteger C1 = C_i[i].mod(N_i).mod(p_0_i);
            BigInteger C1_tag = C_i_tag[i].mod(N_i).mod(p_0_i);
            BigInteger C2_tmp = p.multiply(p_).multiply(C1.mod(q)).add(q.multiply(q_).multiply(C1.mod(p)));
            BigInteger C2_tag_tmp = p.multiply(p_).multiply(C1_tag.mod(q)).add(q.multiply(q_).multiply(C1_tag.mod(p)));
            BigInteger C2 = (GenPrime(pp).multiply(N).add(C2_tmp)).mod(T);
            BigInteger C2_tag = (GenPrime(pp).multiply(N).add(C2_tag_tmp)).mod(T);

            C_ser[i] = (C2.multiply(r_i_).multiply(r)).mod(T);
            C_ser_tag[i] = (C2_tag.multiply(r_i_tag_).multiply(r_tag)).mod(T);
        }

        List<Map> result = new ArrayList<>();
        Map<String, BigInteger[]> CMap = new HashMap<>();
        Map<String, BigInteger> rMap = new HashMap<>();
        rMap.put("r",r);
        rMap.put("r_tag",r_tag);
        CMap.put("C_ser",C_ser);
        CMap.put("C_ser_tag",C_ser_tag);
        result.add(rMap);
        result.add(CMap);

        return result;//<r,r_tag>,<C_ser,C_ser_tag>
    }

    public static BigInteger Cmp(BigInteger Cx, BigInteger Cy, BigInteger r, BigInteger N, BigInteger p1, BigInteger q1, BigInteger N1, BigInteger T1, BigInteger p0, int degF){
        BigInteger n = new BigInteger("2");
        n = n.pow(pp);
        BigInteger r1 = new BigInteger("2");
        BigInteger r2 = n.subtract(new BigInteger("1"));    //n=2^32,r1=2,r2=n-1

        BigInteger p1_ = p1.modInverse(N1);
        BigInteger q1_ = q1.modInverse(N1);
        BigInteger Cx1 = Cx.mod(N);
        BigInteger Cy1 = Cy.mod(N);
        BigInteger k1 = GenPrime(pp),k2 = GenPrime(pp),k3 = GenPrime(pp),k4 = GenPrime(pp);
        BigInteger Cx2_tmp1 = Cx1.add(k1.multiply(p0));
        BigInteger Cx2_tmp2 = p1.multiply(p1_).multiply(Cx2_tmp1.mod(q1)).add(q1.multiply(q1_).multiply(Cx2_tmp1.mod(p1)));
        BigInteger Cx2 = (GenPrime(pp).multiply(N1).add(Cx2_tmp2)).mod(T1);
        BigInteger Cy2_tmp1 = Cy1.add(k1.multiply(p0));
        BigInteger Cy2_tmp2 = p1.multiply(p1_).multiply(Cy2_tmp1.mod(q1)).add(q1.multiply(q1_).multiply(Cy2_tmp1.mod(p1)));
        BigInteger Cy2 = (GenPrime(pp).multiply(N1).add(Cy2_tmp2)).mod(T1);
        BigInteger Cr1 = (p1.multiply(p1_).multiply(r1.mod(q1)).add(q1.multiply(q1_).multiply(r1.mod(p1))).add(GenPrime(pp).multiply(N1))).mod(T1);
        BigInteger Cr2 = (p1.multiply(p1_).multiply(r2.mod(q1)).add(q1.multiply(q1_).multiply(r2.mod(p1))).add(GenPrime(pp).multiply(N1))).mod(T1);

        BigInteger r_ = r.modInverse(T1);
        BigInteger C_x = r_.pow(degF).multiply(Cx2).mod(T1);
        BigInteger C_y = r_.pow(degF).multiply(Cy2).mod(T1);
        int pi = (int) (Math.random()*2);
        BigInteger D = pi==0? (C_x.subtract(C_y)).multiply(Cr1).add(Cr2) : (C_y.subtract(C_x)).multiply(Cr1).add(Cr2).add(Cr1);
        BigInteger d = D.mod(N1).mod(p0);
        BigInteger u0 = BigInteger.valueOf(d.compareTo(n)>0? 0:1);

        return pi==0? u0: BigInteger.valueOf(0);
    }

    public static BigInteger Decrypt(String CF, String Cr, String sk_rec,int degF){
        String S_r = MySM2.decryptSm2(sk_rec, Cr);
        BigInteger r = new BigInteger(S_r);
        String S_F = MySM2.decryptSm2(sk_rec, CF);
        BigInteger F = new BigInteger(S_F);

        return F.divide(r.pow(degF));
    }


}
