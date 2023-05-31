package com.competition.recommend.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;




public class MySM2 {

    public static KeyPair createSm2Key() {
        try {
            //使用标准名称创建EC参数生成的参数规范
            final ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec("sm2p256v1");
            // 获取一个椭圆曲线类型的密钥对生成器
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
            // 使用SM2的算法区域初始化密钥生成器
            kpg.initialize(sm2p256v1, new SecureRandom());
            // 获取密钥对
            return kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String encryptSm2(String publicKeyStr, String data) {
        try {
            //算法工具包
            Security.addProvider(new BouncyCastleProvider());
            //将公钥字符串转为公钥字节
            byte[] bytes = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            CipherParameters pubKeyParameters = new ParametersWithRandom(ECUtil.generatePublicKeyParameter(publicKey), new SecureRandom());
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(true, pubKeyParameters);
            byte[] arrayBytes = sm2Engine.processBlock(data.getBytes(), 0, data.getBytes().length);
            return Base64.encodeBase64String(arrayBytes);
            //开始加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public static String decryptSm2(String privateStr, String data) {
        try {
            byte[] bytes = Base64.decodeBase64(privateStr);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            CipherParameters privateKeyParameters = ECUtil.generatePrivateKeyParameter((BCECPrivateKey) privateKey);
            SM2Engine engine = new SM2Engine();
            engine.init(false, privateKeyParameters);
            byte[] byteDate = engine.processBlock(Base64.decodeBase64(data), 0, Base64.decodeBase64(data).length);
            return new String(byteDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
//        定义需要加密的字符串
        String str = "aaaaa";
        //生成秘钥对
        KeyPair sm2Key = createSm2Key();
        //获取公钥
        PublicKey publicKey = sm2Key.getPublic();
        //获取公钥base加密后字符串
        String publicStr = Base64.encodeBase64String(publicKey.getEncoded());
        System.out.println("公钥为：{}");
        System.out.println(publicStr);
        //获取私钥
        PrivateKey privateKey = sm2Key.getPrivate();
        //获取私钥base加密后字符串
        String privateStr = Base64.encodeBase64String(privateKey.getEncoded());
        System.out.println("私钥为：{}");
        System.out.println(privateStr);

        //公钥加密
        String passStr = encryptSm2(publicStr, str);
        System.out.println("加密后为{}");
        System.out.println(passStr);

        //私钥解密
        String deStr = decryptSm2(privateStr, passStr);
        System.out.println("解密后为{}");
        System.out.println( deStr);

        String pk_ser = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE8ILDmnio0DPTwYGyr3kf/oMl6NLrzEbB2d0PQ+GBTvWRzV7SoaIsOUVVYt8xBQJRZa9lCzJ4aSKDdhgIznENSQ==";
        String pk_csp = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEGkDaiK+vVaXz1SIvTxY5ekbmd1Y36ttkva3FztUEVNy6YVvMGnp//TohyLK4rt3XSghyLu9sB+PwtCDgx9o+ow";
        String sk_ser = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg6jKAhWFFGJwOO9/312//uj2CtrTu561z0n8279IxbJKgCgYIKoEcz1UBgi2hRANCAATwgsOaeKjQM9PBgbKveR/+gyXo0uvMRsHZ3Q9D4YFO9ZHNXtKhoiw5RVVi3zEFAlFlr2ULMnhpIoN2GAjOcQ1J";
        String sk_csp = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgWPxKfkITz3skP9dJbuFK856mQgoHZjPThHzxlqvCnjSgCgYIKoEcz1UBgi2hRANCAAQaQNqIr69VpfPVIi9PFjl6RuZ3Vjfq22S9rcXO1QRU3LphW8waen/9OiHIsriu3ddKCHIu72wH4/C0IODH2j6j";
        String pk_user = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEljt5cNZDyOmhu5pzaQdzqsMQF3QMP/+njlKCPiLC06+Vs3Xa0hqvZqFr3cz7CDjj4omecF8k12ShGErzR5lVlw==";
        String sk_user = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgbaDyPzgbtMO6PlRBYFXke0dS3fI8q4gn8uPZvJaulOSgCgYIKoEcz1UBgi2hRANCAASWO3lw1kPI6aG7mnNpB3OqwxAXdAw//6eOUoI+IsLTr5WzddrSGq9moWvdzPsIOOPiiZ5wXyTXZKEYSvNHmVWX";

        String dec = decryptSm2(sk_ser,"BCBQXOdbCiRCpuRG0DCKQuvnMN4KVnZprduJKcnQhOV89e/6ayq07Y7JTQkwTmhauRY0JQPYroJGcqTS+OE1Yk7c2iIBl1L2TBuqTgBeTbSSFBMRX21I2zGz/gSrcPMpocKqafmVNW3hZ6k=");
        System.out.println(dec);

    }



}


