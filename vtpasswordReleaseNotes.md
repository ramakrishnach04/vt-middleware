

# Version 3.1.2 #
  * [vtpassword-143](http://code.google.com/p/vt-middleware/issues/detail?id=143) - Typo in the TOO\_SHORT message
  * [vtpassword-138](http://code.google.com/p/vt-middleware/issues/detail?id=138) - Fixed wrong parameter order in message.properties for INSUFFICIENT\_CHARACTERISTICS
  * [vtpassword-136](http://code.google.com/p/vt-middleware/issues/detail?id=136) - Add support for salted passwords in HistoryRule

# Version 3.1.1 #
  * [vtpassword-115](http://code.google.com/p/vt-middleware/issues/detail?id=115) - Add rule for specifying a list of allowed characters

# Version 3.1 #
  * [vtpassword-111](http://code.google.com/p/vt-middleware/issues/detail?id=111) - Support custom error messages
    * PasswordValidator changed to contain list of rules, validate method is no longer static
    * RuleResultDetail API changed to support custom message formatting
  * [vtpassword-107](http://code.google.com/p/vt-middleware/issues/detail?id=107) - Added IllegalCharacterRule
  * [vtpassword-105](http://code.google.com/p/vt-middleware/issues/detail?id=105) - CharacterCharacteristicsRule allows inconsistent state

# Version 3.0.1 #
  * [vtpassword-101](http://code.google.com/p/vt-middleware/issues/detail?id=101) - Refactored SequenceRule into separate implementations
    * AlphabeticalSequenceRule
    * NumericalSequenceRule
    * QwertySequenceRule
  * Created new RegexRule and added RepeatCharacterRegexRule, which originated from the now deprecated SequenceRule
  * [vtpassword-102](http://code.google.com/p/vt-middleware/issues/detail?id=102) - vt-dictionary library can now be excluded if no dictionary rules are used

# Version 3.0 #
  * Requires Java 6
  * Refactored rules so that they are **all** stateless, stateful data is now provided in the PasswordData class
  * Break the functionality that was in CharacterRule out into separate rules.
  * Moved PasswordChecker functionality into PasswordValidator
  * Added RuleList which implements Rule and validates a list of rules
  * Change the Rule interface to return an object containing rule results, rather than throwing an exception
  * Remove the 'Password' prefix from all class names

# Version 2.0.2 #
  * [vtpassword-46](http://code.google.com/p/vt-middleware/issues/detail?id=46) - Added password generator implementation

# Version 2.0.1 #
  * Added APL version 2, project is now dual licensed
  * Updated LGPL from version 2.1 to 3.0

# Version 2.0 #
Initial google code release.