package com.vol.solunote.comm.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

/*
 * BasicIdGenerator : 키 생성용 시스템 Generator
 * 
 * mass	/	2016-11-18
 * 
 * Key생성은 시스템 프레임워크에서 가장 중요한 부분이다. 
 * 해당 기능은 가능한 simple key 생성을 통해서 원하는 자리수의 키를 생성해주는 기능을 제공한다.
 * 
 *  단 중복도를 피할 경우에는 uuid를 활용하는 것이 좋다.
 */

public class BasicIdGenerator {

	// array de 64+2 digitos
	private final static char[] DIGITS66 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', '-', ':', '_', ';' };

	public static String next() {
		UUID u = UUID.randomUUID();
		return toIDString(u.getMostSignificantBits()) + toIDString(u.getLeastSignificantBits());
	}

	private static String toIDString(long i) {
		char[] buf = new char[32];
		int z = 64; // 1 << 6;
		int cp = 32;
		long b = z - 1;
		do {
			buf[--cp] = DIGITS66[(int) (i & b)];
			i >>>= 6;
		} while (i != 0);
		return new String(buf, cp, (32 - cp));
	}

	public static long nextLong() {
		long val = -1;
		do {
			final UUID uid = UUID.randomUUID();
			final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
			buffer.putLong(uid.getLeastSignificantBits());
			buffer.putLong(uid.getMostSignificantBits());
			final BigInteger bi = new BigInteger(buffer.array());
			val = bi.longValue();
		} while (val < 0 || val > 9007199254740991L);
		return val;
	}

}