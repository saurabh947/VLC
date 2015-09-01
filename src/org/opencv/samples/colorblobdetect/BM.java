package org.opencv.samples.colorblobdetect;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BM {

	public static boolean search(String text, String pattern) {
		try {
			int radix = 256;
			int M = pattern.length();
			int N = text.length();
			int skip;
			int[] right = new int[radix];
			for (int c = 0; c < radix; c++)
				right[c] = -1;

			for (int j = 0; j < pattern.length(); j++)
				right[pattern.charAt(j)] = j;
			for (int i = 0; i <= N - M; i += skip) {
				skip = 0;
				for (int j = M - 1; j >= 0; j--) {
					if (pattern.charAt(j) != text.charAt(i + j)) {
						try {
							skip = Math.max(1, j - right[text.charAt(i + j)]);
						} catch (Exception e) {

						}
						break;
					}
				}
				if (skip == 0)
					return true; // found
			}

		} catch (Exception e) {

		}
		return false;
	}

//	public static boolean search(List<String> testSentWordList,
//			List<String> corpusSentences, float thresold) {
//
//		float result = 0f;
//		String text;
//		int noOfWordMatches = 0;
//		int noOfWordsInTestSentence = testSentWordList.size();
//		for (int sentencesIndex = 0; sentencesIndex < corpusSentences.size(); sentencesIndex++) {
//			noOfWordMatches = 0;
//			text = corpusSentences.get(sentencesIndex);
//
//			for (int wordIndex = 0; wordIndex < testSentWordList.size(); wordIndex++) {
//
//				if (search(text, testSentWordList.get(wordIndex))) {
//					noOfWordMatches++;
//				}
//			}
//			if ((result = (noOfWordMatches / (float) noOfWordsInTestSentence)) >= thresold) {
//
//				return true;
//			} else {
//
//			}
//
//		}
//
//		return false;
//
//	}

	//public static void BoyerMooreSearch(List<String> testSentences,
//			List<List<String>> listOfCorporaSentences) throws Exception {
//
//		int fileNumber = 0, noOfSentenceMatches = 0, testfileSentenceID = 0, corpusSentenceID = 0, fileID = 0;
//		long startTime, timeTaken;
//		float targetthresold = 0.6f;
//		float targetThreshold = 0.6f;
//		boolean sentenceFound = false;
//		String text, pattern;
//		Map<Integer, Integer> trackedSentenceID = new HashMap<Integer, Integer>();
//		StringBuilder sb = new StringBuilder();
//
//		int noOfTestfileSentences = testSentences.size();
//
//		System.out.println("Boyer Moore : Sentence split");
//		startTime = System.currentTimeMillis();
//
//		for (int testfileSentenceCounter = 0; testfileSentenceCounter < testSentences
//				.size(); testfileSentenceCounter++) {
//			fileNumber = 1;
//			sentenceFound = false;
//			testfileSentenceID++;
//			for (int corpusCounter = 0; corpusCounter < listOfCorporaSentences
//					.size(); corpusCounter++) {
//
//				if (trackedSentenceID.containsKey(fileNumber)) {
//					fileID = fileNumber;
//					corpusSentenceID = trackedSentenceID.get(fileNumber);
//				} else {
//					corpusSentenceID = 0;
//					fileID = corpusCounter;
//				}
//				for (; corpusSentenceID < listOfCorporaSentences.get(
//						corpusCounter).size(); corpusSentenceID++) {
//
//					text = listOfCorporaSentences.get(corpusCounter).get(
//							corpusSentenceID);
//					pattern = testSentences.get(testfileSentenceCounter);
//
//					if (BM.search(text, pattern)) {
//
//						trackedSentenceID.put(fileNumber, corpusSentenceID);
//						noOfSentenceMatches += 1;
//						sentenceFound = true;
//
//						sb.append("the sentence number" + " with id "
//								+ testfileSentenceID
//								+ " from textfile matches with "
//								+ corpusSentenceID + 1 + " from file number "
//								+ fileNumber);
//						sb.append("\n");
//
//						break;
//					}
//				}
//				fileNumber++;
//				if (sentenceFound) {
//					break;
//				}
//			}
//
//		}
//		timeTaken = (System.currentTimeMillis() - startTime);// /1000;
//		FileWriter.write(DetectionMain.output, sb.toString());
//		FileWriter.write(DetectionMain.output, "\n");
//		float matchFactor = noOfSentenceMatches / (float) noOfTestfileSentences;
//
//		System.out.println("MATCH FACTOR= " + matchFactor);
//		if (matchFactor >= targetThreshold)
//			System.out.println("Result: TEST FILE PLAGIARISED");
//		else
//			System.out.println("Result: TEST FILE NOT PLAGIARISED");
//
//		System.out
//				.println("-------------------------------------------------------------------------------------");
//		System.out.println("Boyer Moore : Word Split without stopword removal");
//
//		noOfSentenceMatches = 0;
//		startTime = 0;
//		timeTaken = 0;
//		sentenceFound = false;
//
//		startTime = System.currentTimeMillis();
//		for (int testSentenceIndex = 0; testSentenceIndex < testSentences
//				.size(); testSentenceIndex++) {
//
//			List<String> listofWords = StringUtils
//					.getSplittedWords(testSentences.get(testSentenceIndex));
//
//			for (List<String> fileSentences : listOfCorporaSentences) {
//				fileNumber = 1;
//				sentenceFound = false;
//				if (BM.search(listofWords, fileSentences, 0.6f)) {
//
//					sentenceFound = true;
//					noOfSentenceMatches++;
//					break;
//				}
//				fileNumber++;
//				if (sentenceFound) {
//					break;
//				}
//			}
//
//		}
//		timeTaken = (System.currentTimeMillis() - startTime);// /1000;
//		System.out.println("Time taken = " + timeTaken + " milliseconds ");
//		matchFactor = noOfSentenceMatches / (float) noOfTestfileSentences;
//		System.out.println("MATCHFACTOR : " + matchFactor);
//
//		if (matchFactor >= 0.6)
//			System.out.println("TEXTFILE IS PLAGIARIZED");
//		else
//			System.out.println("TEXTFILE IS NOT PLAGIARIZED");
//
//		System.out
//				.println("-------------------------------------------------------------------------------------");
//		System.out.println("BM : Word Split with StopWords Removal");
//
//		noOfSentenceMatches = 0;
//		fileNumber = 0;
//		startTime = 0;
//		timeTaken = 0;
//
//		startTime = System.currentTimeMillis();
//		for (int testSentenceIndex = 0; testSentenceIndex < testSentences
//				.size(); testSentenceIndex++) {
//
//			List<String> listofWordsWithoutStopWords = StringUtils
//					.getSWordRemovedList(StringUtils
//							.getSplittedWords(testSentences
//									.get(testSentenceIndex)));
//
//			for (int corpusSentencesIndex = 0; corpusSentencesIndex < listOfCorporaSentences
//					.size(); corpusSentencesIndex++) {
//				List<String> fileListsentences = listOfCorporaSentences
//						.get(corpusSentencesIndex);
//
//				if (BM.search(listofWordsWithoutStopWords, fileListsentences,
//						0.3f)) {
//					sentenceFound = true;
//					noOfSentenceMatches++;
//					break;
//				}
//
//				fileNumber++;
//				if (sentenceFound) {
//					break;
//				}
//			}
//		}
//
//		timeTaken = (System.currentTimeMillis() - startTime);// /1000;
//		System.out.println("Time taken = " + timeTaken + " milliseconds ");
//		matchFactor = noOfSentenceMatches / (float) noOfTestfileSentences;
//		System.out.println("MATCHFACTOR : " + matchFactor);
//
//		if (matchFactor >= 0.3)
//			System.out.println("TEXTFILE IS PLAGIARIZED");
//		else
//			System.out.println("TEXTFILE IS NOT PLAGIARIZED");
//
//	}

	public static void main(String[] args) {
		String pat = "a";
		String txt = "?the project gutenberg ebook of the adventures of sherlock holmes by sir arthur conan doyle (#15 in our series by sir arthur conan doyle) copyright laws are changing all over the world. ";

		BM boyermoore1 = new BM();

		boolean result = boyermoore1.search(txt, pat);

		System.out.println("result : " + result);

	}
}