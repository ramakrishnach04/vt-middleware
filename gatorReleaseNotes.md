# Version 1.2.2 #
  * [gator-84](http://code.google.com/p/vt-middleware/issues/detail?id=84) - Fixed critical bug that caused server to lose log messages over time.
  * [gator-81](http://code.google.com/p/vt-middleware/issues/detail?id=81) - Added server-side information and management console.

# Version 1.2.1 #
  * Fixed schema problem with generated log4j XML -- root category must appear last.

# Version 1.2 #
  * [gator-73](http://code.google.com/p/vt-middleware/issues/detail?id=73) - Improve clarity of socket appender configuration by making it explicitly configurable for each category.
  * [gator-72](http://code.google.com/p/vt-middleware/issues/detail?id=72) - Improve server-side event processing by using Java 1.5 Executor facilities.
  * [gator-69](http://code.google.com/p/vt-middleware/issues/detail?id=69) - Add support for log4j additivity flag.
  * [gator-68](http://code.google.com/p/vt-middleware/issues/detail?id=68) - Take advantage of annotation-driven MVC and JSR-303 bean validation.
  * [gator-65](http://code.google.com/p/vt-middleware/issues/detail?id=65) - Upgrade to Spring 3.0.x and Spring Security 3.0.x.
  * Various JPA fixes.
  * Form data validation fixes and improvements.
  * Various usability improvements including navigation and CSS styles.
  * Log configuration fetching at TRACE to reduce gator.log verbosity at DEBUG.
  * Protect against NPE when category has no appenders.

# Version 1.1 #
  * Add security features to project configuration and management supported by Spring Security ACLs.
  * Added testclient and stressclient CLI scripts for fitness and stress testing.

# Version 1.0 #
  * Initial Gator version