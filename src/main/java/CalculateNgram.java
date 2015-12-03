import Utils.StringIgnoreCaseComparator;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nagal_000 on 12/2/2015.
 */
public class CalculateNgram {
    public static void main(String[] args) throws IOException {

        File inputFile = new File("resources/inputFile.txt");

        Comparator<String> caseComparator = new StringIgnoreCaseComparator();

        Map<String, Double> wordCount = new HashMap<String, Double>();
        Map<String, Double> bigramCount = new HashMap<String, Double>();
        Map<String, Double> trigramCount = new HashMap<String, Double>();

        Map<Double, Double> countBigram = new HashMap<Double, Double>();
        Map<Double, Double> countTrigram = new HashMap<Double, Double>();

        Map<String, Double> bigrams = new TreeMap<String, Double>(caseComparator);
        Map<String, Double> trigrams = new TreeMap<String, Double>(caseComparator);

        Map<String, Double> smoothedBigrams = new TreeMap<String, Double>(caseComparator);
        Map<String, Double> smoothedTrigrams = new TreeMap<String, Double>(caseComparator);

        Double totalBigrams = 0.0;  Double totalTrigrams = 0.0;
        Double totalwords = 0.0;
        List<String> strings = new ArrayList<String>();

        try {
            Scanner sc = new Scanner(inputFile);
            while (sc.hasNext()) {
                String str = sc.next();
                Pattern pt = Pattern.compile("[^a-zA-Z0-9']");
                Matcher match= pt.matcher(str);
                while(match.find())
                {
                    String s= match.group();
                    str=str.replaceAll("\\"+s, "");
                }

                strings.add(str);
                if (wordCount.containsKey(str)) {
                    double d = wordCount.get(str);
                    wordCount.put(str, ++d);
                } else {
                    wordCount.put(str, 1.0);
                }
            }

            System.out.println(strings);
            totalwords = (double) strings.size();
            for (int i = 0; i < strings.size(); i++) {
                for (int j = 0; j < strings.size(); j++) {
                    String tmpNext = strings.get(i) + " " + strings.get(j);
                    bigramCount.put(tmpNext, 0.0);
                }
            }

            for (int i = 0; i < strings.size(); i++) {
                String tmp = strings.get(i);
                if (i + 1 < totalwords) {
                    String tmpNext = strings.get(i + 1);
                    tmpNext = tmp + " " + tmpNext;
                    if (bigramCount.containsKey(tmpNext)) {
                        double d = bigramCount.get(tmpNext);
                        bigramCount.put(tmpNext, ++d);
                    } else {
                        bigramCount.put(tmpNext, 1.0);
                    }
                    totalBigrams++;
                }
                if(i+2 <totalwords){
                    String tmpNext = strings.get(i + 1);
                    String tmpNext_ = strings.get(i + 2);
                    tmpNext = tmp + "_" + tmpNext + " " + tmpNext_;
                    if (trigramCount.containsKey(tmpNext)) {
                        double d = trigramCount.get(tmpNext);
                        trigramCount.put(tmpNext, ++d);
                    } else {
                        trigramCount.put(tmpNext, 1.0);
                    }
                    totalTrigrams++;
                }
            }

            System.out.println("Distinct trigrams:" + totalTrigrams);
            countGrams(bigrams, wordCount, bigramCount, countBigram, "");
            countGrams(trigrams, bigramCount, trigramCount, countTrigram, "_");

            calculateSmoothedGrams(bigramCount, countBigram, totalBigrams, smoothedBigrams);
            calculateSmoothedGrams(trigramCount, countTrigram, totalTrigrams, smoothedTrigrams);

            System.out.println("Smoothed bigrams:" + smoothedBigrams);
            System.out.println("Smoothed trigrams:" + smoothedTrigrams);
            System.out.println("unsmoothed bigrams : " + bigrams);
            System.out.println("unsmoothed trigrams : " + trigrams);
            System.out.println("trigram count : " + countTrigram);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    private static void countGrams(Map<String, Double> grams, Map<String, Double> wordCount, Map<String, Double> gramCount, Map<Double, Double> countGram, String splitChar) {
        for (Map.Entry<String, Double> entry : gramCount.entrySet()) {
            String prevWord = entry.getKey().split(" ")[0];
            if(!splitChar.equals(""))
                prevWord = prevWord.replaceAll(splitChar," ");
            Double value = entry.getValue();
            grams.put(entry.getKey(), (value / wordCount.get(prevWord)));
            if (countGram.containsKey(value)) {
                double d = countGram.get(value);
                countGram.put(value, ++d);
            } else {
                countGram.put(value, 1.0);
            }
        }
    }

    private static void calculateSmoothedGrams(Map<String, Double> grams, Map<Double, Double> countGrams,
                                               Double totalwords, Map<String, Double> smoothedGrams) {
        for (Map.Entry<String, Double> entry : grams.entrySet()) {
            Double N = countGrams.get(entry.getValue());
            Double nextN = countGrams.get(entry.getValue() + 1);
            nextN = (nextN == null) ? 0.0 : nextN;
            Double smoothedP = null;
            if (N == 0) {
                smoothedP = nextN / (totalwords);
            } else {
                Double _count = (entry.getValue() + 1) * ((nextN) / N);
                smoothedP = _count / (totalwords);
            }
            smoothedGrams.put(entry.getKey(), smoothedP);
        }
    }

}
