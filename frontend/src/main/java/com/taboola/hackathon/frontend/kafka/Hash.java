package com.taboola.hackathon.frontend.kafka;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.binary.Hex;

import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;


public class Hash {
	public static final String HEXES = "0123456789abcdef";
	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final String HMAC_SHA256 = "HmacSHA256";

	private static final HashFunction s_murmur3_128Function = Hashing.murmur3_128();
	private static final Charset CHARSET = Charset.forName("UTF-8");
	private static final Map<String, ThreadLocal<Mac>> threadLocalHmacMap = createHmacThreadLocalMap();
	private static final ThreadLocal<MessageDigest> threadLocalSha1 = createThreadLocalSha1();
	private static final String SHA_1 = "SHA-1";
	private static final String SHA_256 = "SHA-256";
	private static final ThreadLocal<MessageDigest> threadLocalSha256 = createThreadLocalSha256();
	public static final int SHA_256_STR_LENGTH = 64;

	public static void main(String... args) {
		/*String cleartext =
			args.length < 1  ? new BufferedReader(new InputStreamReader(System.in)).readLine() :
				args[0];
		System.out.println(Hash.sha1NotPadded(cleartext));*/
		int count=1;
		long start=System.currentTimeMillis();
		String text="";
		for (int i=0; i<count;i++) {
//			System.out.println(cityHash(text));
			System.out.println((text).hashCode());
		}
		long end=System.currentTimeMillis();
		System.out.println("Total:" + (end-start));
		System.out.println(String.format("Average: %.10f, %.10f", (end-start)/(double)count, ((end-start)/(double)count) * 1500));
		System.out.println();


        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX");
//        System.out.println(get64BitHashFromTextAndLong("myUrl", 11224322429L));
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX");
	}
	
	public static String sha1NotPadded(String s){
		byte[] bytes = sha1AsBytes(s);
		StringBuilder hash = new StringBuilder(bytes.length);
		for (byte b : bytes){
			hash.append(Integer.toHexString(b&0xff));
		}
		return hash.toString();
	}
	
	public static String sha1(String s){
		return Hex.encodeHexString(sha1AsBytes(s));
	}
	
	public static byte[] sha1AsBytes(String cleartext) {
		return threadLocalSha1.get().digest(cleartext.getBytes(CHARSET));
	}

	public static String sha256(String s) {
		byte[] bytes = threadLocalSha256.get().digest(s.getBytes(CHARSET));
		return Hex.encodeHexString(bytes);
	}

	public static long murmur3based(String text) {
		return s_murmur3_128Function.hashString(text, CHARSET).asLong();
	}
	
//	public static long cityHash(String text) {
//		return CityHash.cityHash128(text.getBytes(CHARSET), 0, text.length())[0];
//	}

//	public static long cityHash64(String text) {
//		return CityHash.cityHash64(text.getBytes(CHARSET), 0, text.length());
//	}
	
	public static String hmacSha1(String secret, String data) {
		return hmac(secret, data, HMAC_SHA1, new Hex());
	}
	
	public static String hmacSha256(String secret, String data) {
        return hmac(secret, data, HMAC_SHA256, new Hex());
	}

	public static String hmacSha256(String secret, String data, BinaryEncoder encoder) {
		return hmac(secret, data, HMAC_SHA256, encoder);
	}

	private static String hmac(String secret, String data, String algo, BinaryEncoder encoder) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(CHARSET), algo);
			Mac mac = threadLocalHmacMap.get(algo).get();
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			byte[] hexBytes = encoder.encode(rawHmac);
			return new String(hexBytes, CHARSET);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	static private Map<String, ThreadLocal<Mac>> createHmacThreadLocalMap() {
		Map<String, ThreadLocal<Mac>> map = Maps.newHashMapWithExpectedSize(2);
		addHmacThreadLocalToMap(map, HMAC_SHA1);
		addHmacThreadLocalToMap(map, HMAC_SHA256);
		return map;
	}

	private static ThreadLocal<MessageDigest> createThreadLocalSha256() {
		return ThreadLocal.withInitial(() -> {
			try {
				return MessageDigest.getInstance(SHA_256);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalArgumentException(e);
			}
		});
	}

	private static void addHmacThreadLocalToMap(Map<String, ThreadLocal<Mac>> map, String algo) {
		ThreadLocal<Mac> hmacThreadLocal = ThreadLocal.withInitial(() -> {
			try {
				return Mac.getInstance(algo);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalArgumentException(e);
			}
		});
		map.put(algo, hmacThreadLocal);
	}

	private static ThreadLocal<MessageDigest> createThreadLocalSha1() {
		return ThreadLocal.withInitial(() -> {
			try {
				return MessageDigest.getInstance(SHA_1);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalArgumentException(e);
			}
		});
	}

//    public static int crc32based(String text) {
//        return Hashing.crc32().hashBytes(text.getBytes(CHARSET)).asInt();
//    }

//    public static long get64BitHashFromTextAndLong(String text, long num) {
//        long stringVal = crc32based(text) & 0xFFFFFFFFL;
//        num = num << 32;
//        long val = stringVal | num;
//        return val;
//    }
//
//	public static long cityHash64FromObjects(Object ... objects) {
//		StringBuilder sb = new StringBuilder();
//		for (Object obj : objects) {
//			sb.append(null == obj ? "_N" : obj.toString()).append("|");
//		}
//		return CityHash.cityHash64(sb.toString().getBytes(CHARSET), 0, sb.length());
//	}

}
 