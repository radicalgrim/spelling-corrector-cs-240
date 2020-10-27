package spell;

public class Trie implements ITrie {
  private final Node root;
  private int wordCount;
  private int nodeCount;
  private int hash;

  public Trie() {
    root = new Node();
    wordCount = 0;
    nodeCount = 1;
    hash = 0;
  }

  @Override
  public void add(String word) {
    final int prime = 31;
    char letter;
    int index;
    Node currentNode = root;

    word = word.toLowerCase();
    hash += prime * word.hashCode();

    for (int i = 0; i < word.length(); i++) {
      letter = word.charAt(i);
      index = letter - 'a';

      if (currentNode.getChildren()[index] == null) {
        currentNode.getChildren()[index] = new Node();
        nodeCount++;
      }
      if (i == word.length() - 1) {
        if (currentNode.getChildren()[index].getValue() == 0) wordCount++;
        currentNode.getChildren()[index].incrementValue();
      }
      currentNode = currentNode.getChildren()[index];
    }
  }

  @Override
  public INode find(String word) {
    char letter;
    int index = 0;
    Node currentNode = root;

    word = word.toLowerCase();

    for (int i = 0; i < word.length(); i++) {
      letter = word.charAt(i);
      index = letter - 'a';
      Node child = currentNode.getChildren()[index];

      if (child == null) return null;
      if (i < word.length() - 1) currentNode = child;
    }

    if (currentNode.getChildren()[index].getValue() > 0) {
      return currentNode.getChildren()[index];
    }

    return null;
  }

  @Override
  public int getWordCount() { return wordCount; }

  @Override
  public int getNodeCount() { return nodeCount; }

  @Override
  public String toString() {
    StringBuilder word = new StringBuilder();
    StringBuilder wordList = new StringBuilder();

    return toStringHelper(root, word, wordList);
  }

  public String toStringHelper(INode currentNode, StringBuilder word, StringBuilder wordList) {
    char letter;

    for (int i = 0; i < currentNode.getChildren().length; i++) {
      INode child = currentNode.getChildren()[i];
      if (child != null) {
        letter = 'a';
        letter += i;
        word.append(letter);
        if (currentNode.getChildren()[i].getValue() > 0) {
          wordList.append(word.toString());
          wordList.append("\n");
        }
        toStringHelper(child, word, wordList);
      }
    }
    if (word.length() > 1) word.deleteCharAt(word.length() - 1);
    else word.setLength(0);

    return wordList.toString();
  }

  @Override
  public int hashCode() { return hash; }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Trie)) return false;
    Trie trie = (Trie) o;
    if (trie.wordCount != this.wordCount) return false;
    if (trie.nodeCount != this.nodeCount) return false;
    if (trie.hash != this.hash) return false;

    return equalsHelper(this.root, trie.root);
  }

  public boolean equalsHelper(INode node1, INode node2) {

    if (node1.getValue() != node2.getValue()) return false;

    for (int i = 0; i < node1.getChildren().length; i++) {
      INode child1 = node1.getChildren()[i];
      INode child2 = node2.getChildren()[i];

      if (child1 != null && child2 != null) {
        equalsHelper(child1, child2);
      }
      else if (child1 != null || child2 != null) return false;
    }
    return true;
  }

}
