package chord;

import java.security.MessageDigest;

//Stolen from joonion @ google code
public class ChordHash {

	public static String function = "SHA-1";

	public static int KEY_LENGTH = 8;
	public static int TABLE_SIZE = (int) (Math.log(KEY_LENGTH)/Math.log(2));
	

	public static byte[] hash(String identifier) {

		if (function.equals("SHA-1")) {
			try {
				MessageDigest md = MessageDigest.getInstance(function);
				md.reset();
				byte[] code = md.digest(identifier.getBytes());
				byte[] value = new byte[KEY_LENGTH / 8];
				int shrink = code.length / value.length;
				int bitCount = 1;
				for (int j = 0; j < code.length * 8; j++) {
					int currBit = ((code[j / 8] & (1 << (j % 8))) >> j % 8);
					if (currBit == 1)
						bitCount++;
					if (((j + 1) % shrink) == 0) {
						int shrinkBit = (bitCount % 2 == 0) ? 0 : 1;
						value[j / shrink / 8] |= (shrinkBit << ((j / shrink) % 8));
						bitCount = 1;
					}
				}
				return value;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (function.equals("Java")) {
			int code = identifier.hashCode();
			code &= (0xffffffff >>> (32 - KEY_LENGTH));
			byte[] value = new byte[KEY_LENGTH / 8];
			for (int i = 0; i < value.length; i++) {
				value[value.length - i - 1] = (byte) ((code >> 8 * i) & 0xff);
			}
			return value;
		}

		return null;
	}

	public static String getFunction() {
		return function;
	}

	public static void setFunction(String function) {
		if (function.equals("SHA-1")) {
			ChordHash.KEY_LENGTH = 160;
		}
		if (function.equals("Java")) {
			ChordHash.KEY_LENGTH = 32;
		}
		ChordHash.function = function;
	}

	public static int getKeyLength() {
		return KEY_LENGTH;
	}

	public static void setKeyLength(int keyLength) {
		KEY_LENGTH = keyLength;
	}

}