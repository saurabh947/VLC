package org.opencv.samples.colorblobdetect;

public class Coding {

	static String frameSeq = "0111100000000000000111111111111111000000001111111100000001111111000000000000000011111111111111110000001111111100000000111111100000000000000011111111111111000000001111111000000011111111000000000000000011111111111111000000001111111000000011111111100000000000000011111111111111100000001111111000000001111111000000000000000011111111111111100000000111111100000001111111100000000000000001111111111111100000000111111100000001111111000000000000000111111111";

	// it is 1-0 data
	int min = 5;
	int max = 8;

	public String getBitPattern(String frameSeq) {
		boolean bitZero = false;
		// boolean validNoofSeq = false;
		int bitIncluded = -1;
		StringBuilder sb = new StringBuilder();
		char initSeq = frameSeq.charAt(0);
		int count = 0, index = 0;
		while (index < frameSeq.length()) {
			bitIncluded = -1;

			if (initSeq == frameSeq.charAt(index)) {
				count++;
				index++;
			} else {
				bitIncluded = isValidNoofSeq(index);
				if (bitIncluded > 0) {
					for (int i = 0; i < bitIncluded; i++) {
						sb.append(frameSeq.charAt(initSeq));
					}
				}
				initSeq = frameSeq.charAt(index);
				count = 0;
			}
		}
		System.out.println("ODEGENERATED=====>>> " + sb.toString());
		return sb.toString();
	}

	public int isValidNoofSeq(int count) {
		if (count >= 5 || count <= 8) {
			return 1;
		} else if (count >= 10 || count <= 16) {
			return 2;
		} else {
			return -100;
		}
	}

	public static void main(String[] args) {
		Coding c = new Coding();
		String bitdecoded = c.getBitPattern(frameSeq);
		System.out.println(bitdecoded);
	}
}
