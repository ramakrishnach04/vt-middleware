

# Introduction #
VT Dictionary is a Java library for searching and sorting a list of words.<br />
Two implementations of a dictionary are supplied, [WordListDictionary](http://vt-middleware.googlecode.com/svn/vt-dictionary/javadoc/vt-dictionary-3.0/edu/vt/middleware/dictionary/WordListDictionary.html) and [TernaryTreeDictionary](http://vt-middleware.googlecode.com/svn/vt-dictionary/javadoc/vt-dictionary-3.0/edu/vt/middleware/dictionary/TernaryTreeDictionary.html).


---

# Installation #
The latest version can be downloaded from the [downloads](http://code.google.com/p/vt-middleware/downloads/list) page.

This project is available from Maven Central. If you would like to use this project in your maven build, include the following in your pom.xml:
```
<dependencies>
  <dependency>
      <groupId>edu.vt.middleware</groupId>
      <artifactId>vt-dictionary</artifactId>
      <version>3.0</version>
  </dependency>
<dependencies>
```


---

# Code Samples #
Unless otherwise noted, classes appearing in the following samples are included in the JSE libraries or VT Dictionary.

## WordListDictionary ##
This dictionary is implemented using a WordList, which allows the user to decide how the word list should be backed. The only type of search that is supported is exact match. The following implementations of WordList are provided:
  * [ArrayWordList](http://vt-middleware.googlecode.com/svn/vt-dictionary/javadoc/vt-dictionary-3.0/edu/vt/middleware/dictionary/ArrayWordList.html) - words are read and stored in an array
  * [FileWordList](http://vt-middleware.googlecode.com/svn/vt-dictionary/javadoc/vt-dictionary-3.0/edu/vt/middleware/dictionary/FileWordList.html) - words are read from a file, caching is supported and recommended for performance

WordLists can be configured to support searching in both case sensitive and insensitive modes, but once a word list has been constructed its case behavior is immutable.  The case behavior is inherited by the WordListDictionary that uses it.

### ArrayWordList example ###
```
// create a new word list from the supplied file
// file is expected to be sorted
// by default the word list is case sensitive
ArrayWordList awl = WordLists.createFromReader(
  new FileReader[] {new FileReader("/path/to/my/words")});
WordListDictionary dict = new WordListDictionary(awl);
if (dict.search("chalice")) {
  System.out.println("chalice was found in this dictionary");
} else {
  System.out.println("chalice was not found in this dictionary");
}
```

### FileWordList example ###
```
// create a new word list from the supplied file
// file is expected to be sorted
// by default the word list is case sensitive
// by default 5% of the file is cached
FileWordList fwl = new FileWordList(new RandomAccessFile("/path/to/my/file", "r"))
WordListDictionary dict = new WordListDictionary(fwl);
if (dict.search("chalice")) {
  System.out.println("chalice was found in this dictionary");
} else {
  System.out.println("chalice was not found in this dictionary");
}
```

## TernaryTreeDictionary ##
This dictionary is implemented using a [ternary tree](http://en.wikipedia.org/wiki/Ternary_search_tries), and supports the following types of searches:
  * exact match
  * partial search
  * near search.
The sorting functions are not necessary, however sorting your data and inserting from the median produces a more balanced tree and reduces search time.<br />
Note that this implementation requires an in-memory ternary tree, which can require large amounts of heap space.

### Exact Search ###
Search for an exact match on a search term.<br />
For instance, using the supplied Webster's Second International Dictionary with a search term of `chalice` returns true.

```
ArrayWordList awl = WordLists.createFromReader(
  new FileReader[] {new FileReader("/path/to/my/words")});
// creates a new ternary tree by reading the words in the word list
// by default the ternary tree is created using the median of the word list
TernaryTreeDictionary dict = new TernaryTreeDictionary(awl);
if (dict.search("chalice")) {
  System.out.println("chalice was found in this dictionary");
} else {
  System.out.println("chalice was not found in this dictionary");
}
```

### Partial Search ###
Search for word(s) that partially match a search term.<br />
The dot character (.) is used to represent any valid character.<br />
For instance, using the supplied Webster's Second International Dictionary with a search term of `.l.st.n` yields the following results:
  * elastin
  * glisten
  * plastin

```
ArrayWordList awl = WordLists.createFromReader(
  new FileReader[] {new FileReader("/path/to/my/words")});
// creates a new ternary tree by reading the words in the word list
// by default the ternary tree is created using the median of the word list
TernaryTreeDictionary dict = new TernaryTreeDictionary(awl);
String[] results = dict.partialSearch(".l.st.n");
if (results.length > 0) {
  System.out.println(".l.st.n was found in this dictionary:");
  System.out.println(Arrays.asList(results));
} else {
  System.out.println(".l.st.n was not found in this dictionary");
}
```

Note that case-insensitive searching is **not** supported for partial searches. To achieve this behavior you must lower-case all your words and then lower-case your search input.

### Near Search ###
Search for word(s) that are _near_ a search term by a certain distance.<br />
For instance, using the supplied Webster's Second International Dictionary with a search term of `display` at a distance of `2` yields the following results:
  * disally
  * displace
  * displant
  * display
  * displayed
  * displayer
  * misplay

```
ArrayWordList awl = WordLists.createFromReader(
  new FileReader[] {new FileReader("/path/to/my/words")});
// creates a new ternary tree by reading the words in the word list
// by default the ternary tree is created using the median of the word list
TernaryTreeDictionary dict = new TernaryTreeDictionary(awl);
String[] results = dict.nearSearch("display", 2);
if (results.length > 0) {
  System.out.println("display was found in this dictionary:");
  System.out.println(Arrays.asList(results));
} else {
  System.out.println("display was not found in this dictionary");
}
```

Note that case-insensitive searching is **not** supported for near searches. To achieve this behavior you must lower-case all your words and then lower-case your search input.


---

# Scripts #
Script execution requirements vary by platform.  For the following platform-specific instructions, let VTDICT\_HOME be the location where the VT Dictionary distribution was unpacked.

**Unix**
  1. Ensure the java executable is on your path.
  1. Ensure $VTDICT\_HOME/bin is on your path.
  1. If you encounter classpath problems executing the scripts, export VTDICT\_HOME as a separate shell variable.  This is not necessary in most cases (e.g. Linux, OSX, FreeBSD).

## ternarytree - Search Operations ##
Perform a search for the term `chalice`
```
bin/ternarytree dict/web2 -m -s chalice
```

Perform a partial search for the term `.l.st.n`
```
bin/ternarytree dict/web2 -m -ps .l.st.n
```

Perform a near search for the term `display` at a distance of `2`
```
bin/ternarytree dict/web2 -m -ns display 2
```

Print command-line usage statement
```
bin/ternarytree -h
```


---

# Performance Considerations #
The TernaryTreeDictionary tends to perform marginally faster on exact searches, however it incurs the largest memory footprint. It should also be noted that performance is relative to how well your ternary tree is balanced. The WordListDictionary leverages a binary search algorithm and performs almost as well as TernaryTreeDictionary, but uses slightly less memory when backed by an ArrayWordList. A WordListDictionary backed by a FileWordList will perform in proportion to the amount of caching that is configured, allowing the user to balance memory consumption against speed.<br /><br />
In general if you are just performing exact searches, a WordListDictionary backed by an ArrayWordList is recommended. If you need partial and near search functionality, TernaryTreeDictionary is your only option.


---

# Available Dictionaries #
  * [ftp://ftp.ox.ac.uk/pub/wordlists/](ftp://ftp.ox.ac.uk/pub/wordlists/)
  * http://icon.shef.ac.uk/Moby/
  * http://packetstormsecurity.nl/Crackers/wordlists/dictionaries/