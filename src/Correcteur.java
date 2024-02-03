import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Correcteur {
    private final LinkedList<String> dictionary = new LinkedList<>();
    private final HashMap<String, LinkedList<String>> trigramsAndRelatedWords = new HashMap<>();
    public final HashMap<Integer, LinkedList<String>> levenshteinDistances = new HashMap<>();

    public Correcteur() throws FileNotFoundException {
        this.initDictionary();
        this.buildTrigramMap();
//        printMapStatus();
    }

    private void initDictionary() throws FileNotFoundException {
        Scanner dictReader = new Scanner(new File("dico.txt"));
        while (dictReader.hasNextLine()) {
            String dictWord = dictReader.nextLine();
            this.dictionary.addLast(dictWord);
        }
    }
    /* ______________ */
    private void buildTrigramMap() {
        int dictSize = this.dictionary.size();
        for (int i = 0; i < dictSize; i++) {
//            String dictWord = this.dictionary.removeFirst();
            this.addTrigrams(this.dictionary.removeFirst());
        }
    }
    private void addTrigrams(String word) {
        String wordForTrigrams = '<' + word + '>';
        int wordSize = wordForTrigrams.length();
        for (int i = 0; i < wordSize-2; i++) {
            String trigram = wordForTrigrams.substring(i, i+3);
            if (!trigramsAndRelatedWords.containsKey(trigram)) {
//                this.trigramsAndRelatedWords.keySet().add(trigram); // On ajoute 'trigram' une nouvelle clé dans ce cas
                this.trigramsAndRelatedWords.put(trigram, new LinkedList<>());
            }
            /*if (!this.trigramsAndRelatedWords.get(trigram).contains(word)) {
                this.trigramsAndRelatedWords.get(trigram).add(word);
            }*/
            this.trigramsAndRelatedWords.get(trigram).add(word);
        }
    }
    /* ______________ */
    public List<String> getWordWithCommonTrigrams(String word) {
        LinkedList<String> trigramsOfWord = getTrigrams(word);
        List<String> wordWithCommonTrigrams = new ArrayList<>();
        while (!trigramsOfWord.isEmpty()) {
            String trigramOfWord = trigramsOfWord.removeFirst();
            for (String trigramOfRelatedMap : this.trigramsAndRelatedWords.keySet()) {
                if (trigramOfRelatedMap.equals(trigramOfWord)) {
                    wordWithCommonTrigrams.addAll(this.trigramsAndRelatedWords.get(trigramOfWord));
                }
            }
        }
        return wordWithCommonTrigrams;
    }
    private LinkedList<String> getTrigrams(String word) {
        String wordForTrigrams = '<' + word + '>';
        int wordSize = wordForTrigrams.length();
        LinkedList<String> trigrams = new LinkedList<>();
        for (int i = 0; i < wordSize-2; i++) {
            trigrams.add(wordForTrigrams.substring(i, i+3));
        }
        return trigrams;
    }
    /* ______________ */
    public HashMap<Integer, Set<String>> getAllWordsFrequencies(List<String> allWords) {
        HashMap<Integer, Set<String>> allWordsCount = new HashMap<>();
        for (String s : allWords) {
            int count = Collections.frequency(allWords, s); // Count the frequency of a specific word
            if (!allWordsCount.containsKey(count)) {
                allWordsCount.put(count, new HashSet<>());
            }
            allWordsCount.get(count).add(s);
        }
        return allWordsCount;
    }
    public LinkedList<String> getTopHundred(HashMap<Integer, Set<String>> allWordsFrequencies) {
        List<Integer> frequencyList = new ArrayList<>(allWordsFrequencies.keySet());
//        System.out.println(frequencyList);
        LinkedList<String> topHundred = new LinkedList<>();
        for (int i = allWordsFrequencies.size()-1; i >= 0; i--) {
//            System.out.println(frequencyList.get(i));
//            System.out.println(allWordsFrequencies.get(frequencyList.get(i)));
            topHundred.addAll(allWordsFrequencies.get(frequencyList.get(i)));
            if (topHundred.size() >= 100) {
                while (topHundred.size() > 100) {
                    topHundred.removeLast();
                }
//                System.out.println(topHundred.size());
                break;
            }
        }
        return topHundred;
    }
    /* -------------- */
    public void buildLevenshteinMap(LinkedList<String> topHundred, String requested) {
        for (int i = 0; i < 100; i++) {
            String topHundredWord = topHundred.removeFirst();
            int distance = computeLevenshteinDistance(requested, topHundredWord);

            if (!this.levenshteinDistances.containsKey(distance)) {
                this.levenshteinDistances.put(distance, new LinkedList<>());
            }
            this.levenshteinDistances.get(distance).addLast(topHundredWord);

        }
    }
    public int computeLevenshteinDistance(String requested, String word) {
        int[][] T = new int[requested.length() + 1][word.length() + 1];
        for (int i = 0; i < requested.length() + 1; i++) {
            T[i][0] = i;
        }
        for (int j = 0; j < word.length() + 1; j++) {
            T[0][j] = j;
        }
        for (int i = 1; i < requested.length() + 1; i++) {
            for (int j = 1; j < word.length() + 1; j++) {
                if (requested.charAt(i-1) == word.charAt(j-1)) {
                    T[i][j] = T[i-1][j-1];
                }
                else {
                    T[i][j] = Math.min(Math.min(T[i-1][j],T[i][j-1]),T[i-1][j-1]) +1;
                }
            }
        }
        return T[requested.length()][word.length()];
    }
    public List<String> getTopFive() {
        List<Integer> distancesList = new ArrayList<>(this.levenshteinDistances.keySet());
//        System.out.println(distancesList);
        List<String> topFive = new ArrayList<>();
        for (int i = 0; i < this.levenshteinDistances.size(); i++) {
//            System.out.println(this.levenshteinDistances.get(distancesList.get(i)));
            topFive.addAll(this.levenshteinDistances.get(distancesList.get(i)));
            if (topFive.size() >= 5) {
                while (topFive.size() > 5) {
                    topFive.remove(topFive.size()-1);
                }
//                System.out.println(topFive.size());
                break;
            }
        }
        return topFive;
    }



    public void correctWord(String requested) {
        List<String> l = getWordWithCommonTrigrams(requested);
        HashMap<Integer, Set<String>> hm = getAllWordsFrequencies(l);
        LinkedList<String> top100 = getTopHundred(hm);
        buildLevenshteinMap(top100,requested);
        List<String> top5 = getTopFive();
        System.out.println(top5);
    }






    public void printDictionaryStatus() {
        System.out.println(this.dictionary);
        System.out.println(this.dictionary.size() + '\n');
    }

    public void printMapStatus() {
        /*for (String s : this.trigramsAndRelatedWords.keySet()) {
            System.out.println("key" + s);
        }*/
        System.out.println(this.trigramsAndRelatedWords);
    }




}
