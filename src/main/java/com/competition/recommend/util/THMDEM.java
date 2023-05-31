package com.competition.recommend.util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static com.competition.recommend.util.MySM2.createSm2Key;

public class THMDEM {
    private static final int pp = 32;
    private static final BigInteger System_p = new BigInteger("2546898083");
    private static final BigInteger System_q = new BigInteger("4114078093");
    private static final BigInteger System_p_ = System_p.modInverse(System_q);
    private static final BigInteger System_q_ = System_q.modInverse(System_p);
    private static final BigInteger System_s= new BigInteger("3386846423");
    private static final BigInteger System_N= new BigInteger("10478137608373995719");
    private static final BigInteger System_T= new BigInteger("35487842878623242247112463137");
    private static final BigInteger System_p_double= new BigInteger("10573295537407891733");
    private static final BigInteger System_q_double= new BigInteger("13116557318305684223");
    private static final BigInteger System_s_double= new BigInteger("13818324266098118957");
    private static final BigInteger System_N_double= new BigInteger("138685236959796314692334750912470228459");
    private static final BigInteger System_T_double= new BigInteger("1916397575231121132532254678058908661571949863203948797263");
    private static final BigInteger System_p0= new BigInteger("2665505220591720907");
    public static final String pk_ser = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE8ILDmnio0DPTwYGyr3kf/oMl6NLrzEbB2d0PQ+GBTvWRzV7SoaIsOUVVYt8xBQJRZa9lCzJ4aSKDdhgIznENSQ==";
    public static final String pk_csp = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEGkDaiK+vVaXz1SIvTxY5ekbmd1Y36ttkva3FztUEVNy6YVvMGnp//TohyLK4rt3XSghyLu9sB+PwtCDgx9o+ow";
    public static final String sk_ser = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg6jKAhWFFGJwOO9/312//uj2CtrTu561z0n8279IxbJKgCgYIKoEcz1UBgi2hRANCAATwgsOaeKjQM9PBgbKveR/+gyXo0uvMRsHZ3Q9D4YFO9ZHNXtKhoiw5RVVi3zEFAlFlr2ULMnhpIoN2GAjOcQ1J";
    public static final String sk_csp = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgWPxKfkITz3skP9dJbuFK856mQgoHZjPThHzxlqvCnjSgCgYIKoEcz1UBgi2hRANCAAQaQNqIr69VpfPVIi9PFjl6RuZ3Vjfq22S9rcXO1QRU3LphW8waen/9OiHIsriu3ddKCHIu72wH4/C0IODH2j6j";



    public static BigInteger GenPrime(int length){
        Random r = new Random();
        return BigInteger.probablePrime(length,r);

    }

    public static BigInteger[] UserKeyGen(){
        BigInteger[] result = new BigInteger[8];

        result[0] = GenPrime(pp);//p
        result[1] = GenPrime(pp);//q
        result[2] = GenPrime(pp);//s
        result[3] = result[0].multiply(result[1]);//N
        result[4] = result[0].multiply(result[1]).multiply(result[2]);//T
        result[5] = GenPrime(pp-1);//p_0
        result[6] = GenPrime(pp);//r
        result[7] = GenPrime(pp);//r_tag

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
        result[10]=GenPrime(2*pp-2);
        return result;  //<p,q,s,N,T,p',q',s',N',T',p0> 前五个计算函数用，后六个比较算法用。
    }

    public static Map<String,String> Encrypt(int m, BigInteger p, BigInteger q, BigInteger N, BigInteger T, BigInteger p_0,BigInteger r,BigInteger r_tag, String pk_ser, String pk_csp) {

        int s = m == 0 ? 1 : 0;

        BigInteger p_ = p.modInverse(q),q_=q.modInverse(p);
        BigInteger msp = BigInteger.valueOf(m + s).add(GenPrime(pp).multiply(p_0));
        BigInteger tmp1 = p.multiply(p_).multiply(msp).add(q.multiply(q_).multiply(msp));
        BigInteger C = tmp1.multiply(r).add(GenPrime(pp).multiply(N)).mod(T);
        BigInteger add = GenPrime(pp).pow(s).add(GenPrime(pp).multiply(p_0));
        BigInteger tmp2 = p.multiply(p_).multiply(add).add(q.multiply(q_).multiply(add));
        BigInteger C_tag = tmp2.multiply(r_tag).add(GenPrime(pp).multiply(N)).mod(T);

        String C_ser = MySM2.encryptSm2(pk_ser, String.valueOf(r));
        String C_ser_tag = MySM2.encryptSm2(pk_ser, String.valueOf(r_tag));
        String C_csp = MySM2.encryptSm2(pk_csp, String.valueOf(N));

        Map<String, String> map = new HashMap<>();
        map.put("C_ser",C_ser);
        map.put("C_ser_tag", C_ser_tag);
        map.put("C_csp",C_csp);
        map.put("C",C.toString());
        map.put("C_tag",C_tag.toString());


        return map;//<C_ser,C_ser_tag,C_csp,C,C_tag>
    }

    public static Map<String,BigInteger> KeySwitch(Map<String,String> C_sen, BigInteger p_0_i, String sk_ser, String sk_csp){
        String S_r_i = MySM2.decryptSm2(sk_ser, (String) C_sen.get("C_ser"));
        String S_r_i_tag = MySM2.decryptSm2(sk_ser, (String) C_sen.get("C_ser_tag"));
        String S_N_i = MySM2.decryptSm2(sk_csp, (String) C_sen.get("C_csp"));
        BigInteger r_i = new BigInteger(S_r_i);
        BigInteger r_i_tag = new BigInteger(S_r_i_tag);
        BigInteger N_i = new BigInteger(S_N_i);

        BigInteger r_i_ = r_i.modInverse(N_i);
        BigInteger r_i_tag_ = r_i_tag.modInverse(N_i);

        BigInteger C_i = new BigInteger(C_sen.get("C"));
        BigInteger C_i_tag = new BigInteger(C_sen.get("C_tag"));


        BigInteger r = GenPrime(pp);
        BigInteger r_tag = GenPrime(pp);

        BigInteger C1 = C_i.multiply(r_i_).mod(N_i).mod(p_0_i);
        BigInteger C1_tag = C_i_tag.multiply(r_i_tag_).mod(N_i).mod(p_0_i);
        BigInteger C2_tmp = System_p.multiply(System_p_).multiply(C1).add(System_q.multiply(System_q_).multiply(C1));
        BigInteger C2_tag_tmp = System_p.multiply(System_p_).multiply(C1_tag).add(System_q.multiply(System_q_).multiply(C1_tag));
        BigInteger C_ser = (C2_tmp.multiply(r).add(GenPrime(pp).multiply(System_N))).mod(System_T);
        BigInteger C_ser_tag = (C2_tag_tmp.multiply(r_tag).add(GenPrime(pp).multiply(System_N))).mod(System_T);

        Map<String, BigInteger> map = new HashMap<>();
        map.put("r",r);
        map.put("r_tag",r_tag);
        map.put("C_ser",C_ser);
        map.put("C_ser_tag",C_ser_tag);

        return map;//<r,r_tag,C_ser,C_ser_tag>
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

    public static void main(String[] args) {
        int[] m = new int[2];
        m[0] = 2;
        m[1] = 2;
        String pk_user = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEljt5cNZDyOmhu5pzaQdzqsMQF3QMP/+njlKCPiLC06+Vs3Xa0hqvZqFr3cz7CDjj4omecF8k12ShGErzR5lVlw==";
        String sk_user = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgbaDyPzgbtMO6PlRBYFXke0dS3fI8q4gn8uPZvJaulOSgCgYIKoEcz1UBgi2hRANCAASWO3lw1kPI6aG7mnNpB3OqwxAXdAw//6eOUoI+IsLTr5WzddrSGq9moWvdzPsIOOPiiZ5wXyTXZKEYSvNHmVWX";







    }
}
