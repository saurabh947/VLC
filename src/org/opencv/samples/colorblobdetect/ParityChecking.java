package org.opencv.samples.colorblobdetect;

import java.util.ArrayList;
import java.util.List;

public class ParityChecking {
	double errorRate;

	public String checkParity(char[] input) {
		List<Integer> SFDPos = new ArrayList<Integer>();
		List<Data> data = new ArrayList<Data>();// with parity
		int sum = 0;
		int errorBlockCount = 0;
		int totalBlockCount = 0;
		boolean SFDFound = false;
		StringBuilder sb = new StringBuilder();
		int currentIndex = 0;

		for (int i = 0; i < input.length; i++) {
			SFDFound = false;

			sum = 0;
			if (input[i] == '1') {
				if (((i + 1) < input.length) && input[i + 1] == '0') {
					if (((i + 2) < input.length) && input[i + 2] == '1') {
						SFDPos.add(i);
						if (i != currentIndex) {
							errorBlockCount++;
							totalBlockCount++;
						}
						SFDFound = true;
					}
				}
			}
			if (SFDFound && (i + 3) < input.length && (i + 8) <= input.length) {
				// so from i+3 to i+8 is our data sswith parity
				for (int dataindex = i + 3; ((i + 8) < input.length && dataindex <= (i + 8)); dataindex++) {
					sb.append(input[dataindex]);
					if (input[dataindex] == '1') {
						sum += Integer.parseInt(input[dataindex] + "");
					}
					if (dataindex == (i + 8)) {
						currentIndex = i + 9;
					}
				}

				if (sum % 2 == 0) {
					// parity validated in this data and hence validity is true
					data.add(new Data(sb.toString(), true));
					sb.delete(0, sb.length());
				} else {

					// parity invalidated in this data
					data.add(new Data(sb.toString(), false));
					sb.delete(0, sb.length());
					errorBlockCount++;
				}
				totalBlockCount++;

			}else if(SFDFound && ((i + 8) > input.length)){
				errorBlockCount++;
				totalBlockCount++;
			}
		}

		// calculate Block Error Rate
		errorRate = errorBlockCount / (double) totalBlockCount;
		System.out.println("ErrorRate==>" + errorRate);

		if (errorRate < 0.4) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).isValid) {
					return data.get(i).data;
				}
			}
		} else {
			return "000000";
		}
		return "111111";
	}

	

	class Data {
		boolean isValid;
		String data;

		Data(String data, boolean validity) {
			this.data = data;
			this.isValid = validity;
		}

	}

	public double getErrorRate(){
		return errorRate;
	}
	public static void main(String[] args) {
	}
}
