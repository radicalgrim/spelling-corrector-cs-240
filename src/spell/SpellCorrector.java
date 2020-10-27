package spell;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeSet;

public class SpellCorrector implements ISpellCorrector {
  private final Trie dictionary;
  private final TreeSet<String> candidates;
  private final TreeSet<String> validCandidates;

  public SpellCorrector() {
    dictionary = new Trie();
    candidates = new TreeSet<String>();
    validCandidates = new TreeSet<String>();
  }

  @Override
  public void useDictionary(String dictionaryFileName) throws IOException {
    File file = new File(dictionaryFileName);
    processFile(file);
  }

  public void processFile(File file) throws IOException {
    Scanner scanner = new Scanner(file);
    scanner.useDelimiter("(#[^\\n]*\\n)|(\\s+)+");

    while(scanner.hasNext()) {
      String str = scanner.next();
      dictionary.add(str);
    }
  }
  
  @Override
  public String suggestSimilarWord(String inputWord) {
    if (inputWord.equals("")) return null;

    inputWord = inputWord.toLowerCase();
    if (dictionary.find(inputWord) != null) return inputWord;

    candidates.clear();
    validCandidates.clear();

    deletionCheck(inputWord);
    transpositionCheck(inputWord);
    alterationCheck(inputWord);
    insertionCheck(inputWord);

    String mostCommonWord;
    mostCommonWord = validateCandidates();

    if (validCandidates.isEmpty()) {

      TreeSet<String> clone_candidates = (TreeSet)candidates.clone();

      for (String str : clone_candidates) {
        deletionCheck(str);
        transpositionCheck(str);
        alterationCheck(str);
        insertionCheck(str);
      }
      mostCommonWord = validateCandidates();
      if (validCandidates.isEmpty()) return null;
    }

    if (validCandidates.size() == 1) return validCandidates.first();

    return mostCommonWord;
  }

  public void deletionCheck(String word) {
    if (word.length() < 2) return;
    StringBuilder newWord = new StringBuilder();
    for (int i = 0; i < word.length(); i++) {
      newWord.append(word);
      newWord.deleteCharAt(i);
      candidates.add(newWord.toString());
      newWord.setLength(0);
    }
  }

  public void transpositionCheck(String word) {
    if (word.length() < 2) return;
    StringBuilder newWord = new StringBuilder();
    StringBuilder subString = new StringBuilder();

    for (int i = 0; i < word.length() - 1; i++) {
      newWord.append(word);
      subString.append(newWord.substring(i, i+2));
      subString.reverse();
      newWord.replace(i,i + 2, subString.toString());
      candidates.add(newWord.toString());
      newWord.setLength(0);
      subString.setLength(0);
    }
  }

  public void alterationCheck(String word) {
    StringBuilder newWord = new StringBuilder();
    char letter;

    for (int i = 0; i < word.length(); i++) {
      for (int j = 0; j < 26; j++) {
        letter = 'a';
        letter += j;
        newWord.append(word);
        newWord.setCharAt(i, letter);
        candidates.add(newWord.toString());
        newWord.setLength(0);
      }
    }

  }

  public void insertionCheck(String word) {
    StringBuilder newWord = new StringBuilder();
    char letter;

    for (int i = 0; i < word.length() + 1; i++) {
      for (int j = 0; j < 26; j++) {
        letter = 'a';
        letter += j;
        newWord.append(word);
        newWord.insert(i, letter);
        candidates.add(newWord.toString());
        newWord.setLength(0);
      }
    }

  }

  public String validateCandidates() {
    int max = 0;
    INode tempNode;
    String mostCommonWord = null;

    for (String str : candidates) {
      tempNode = dictionary.find(str);
      if (tempNode != null) {
        validCandidates.add(str);
        if (tempNode.getValue() > max) {
          max = tempNode.getValue();
          mostCommonWord = str;
        }
      }
    }
    return mostCommonWord;
  }

}
