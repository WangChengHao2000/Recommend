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

    }



}


