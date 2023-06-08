import java.util.ArrayList;
import java.util.Arrays;
import tester.Tester;

//A Vigenere Cioher
class Vigenere {
  ArrayList<Character> alphabet;
  ArrayList<ArrayList<Character>> table;

  Vigenere() {
    this.alphabet = new ArrayList<Character>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

    this.table = initVigenere();
  }

  // Initialize Vigenere
  ArrayList<ArrayList<Character>> initVigenere() {
    ArrayList<ArrayList<Character>> table = new ArrayList<ArrayList<Character>>();
    for (char i : alphabet) {
      ArrayList<Character> row = new ArrayList<Character>();
      for (char j : alphabet) {
        int index = (alphabet.indexOf(i) + alphabet.indexOf(j)) % alphabet.size();
        row.add(alphabet.get(index));
      }
      table.add(row);
    }
    return table;
  }

  // generates a key
  String key(String plaintext, String keyword) {
    String key = "";
    int j = 0;
    for (char c : plaintext.toCharArray()) {
      if (j == keyword.length()) {
        j = 0;
      }
      key += (keyword.charAt(j));
      j++;
    }
    return key;
  }

  // Encodes a message in the Vigenere cipher
  String encode(String plaintext, String keyword) {
    String ciphertext = "";
    String key = key(plaintext, keyword);
    int i = 0;
    for (char mess : plaintext.toCharArray()) {
      char ke = key.charAt(i++);
      int row = this.alphabet.indexOf(ke);
      int col = this.alphabet.indexOf(mess);
      ciphertext += this.table.get(row).get(col);
    }
    return ciphertext;
  }

  // decodes message in vigenere cipher
  String decode(String ciphertext, String keyword) {
    String plaintext = "";
    String key = key(ciphertext, keyword);
    int index = 0;
    for (char mess : ciphertext.toCharArray()) {
      char ke = key.charAt(index++);
      int row = this.alphabet.indexOf(ke);
      int column = this.table.get(row).indexOf(mess);
      plaintext += this.alphabet.get(column);
    }
    return plaintext;
  }
}

// Vigenere Examples and tests 
class VigenereExamples {
  Vigenere v = new Vigenere();

  // test decode
  void testdecode(Tester t) {
    t.checkExpect(v.decode("ahpcizgxkgug", "happy"), "thanksgiving");
    t.checkExpect(v.decode("", "ha"), "");
    t.checkExpect(v.decode("begxfmg", "key"), "rainbow");
  }

  // test encode
  void testencode(Tester t) {
    t.checkExpect(v.encode("thanksgiving", "happy"), "ahpcizgxkgug");
    t.checkExpect(v.decode("", "happy"), "");
    t.checkExpect(v.decode("sleepisnice", "fundies"), "nrrbheaiopb");
  }

  // test key
  void testkey(Tester t) {
    t.checkExpect(v.key("thanksgiving", "happy"), "happyhappyha");
    t.checkExpect(v.key("", ""), "");
    t.checkExpect(v.key("sleepisnice", "fundies"), "fundiesfund");
  }

  // test initVigenere
  void testinitVigenere(Tester t) {
    ArrayList<Character> alphabet = new ArrayList<Character>(
        Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

    ArrayList<Character> table = new ArrayList<Character>(25);
  }
}
