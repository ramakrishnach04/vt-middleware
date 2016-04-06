

# Introduction #
This library provides support for pooling ldap objects.<br />
Pooling ldap objects provides a way to mitigate the overhead of creating a LDAP connection for every operation, especially when performance is critical.<br />
JNDI provides it's own [pooling implementation](http://java.sun.com/products/jndi/tutorial/ldap/connect/pool.html), but it is limited in it's configuration options and most importantly does not support pooling of TLS connections.<br />
The main design requirement for these implementations is to provide a pool that does not block when creating or closing ldap connections.


---

# Properties #
| **Property Name** | **Default Value** | **Description** |
|:------------------|:------------------|:----------------|
| edu.vt.middleware.ldap.pool.minPoolSize | 3                 | Size the pool should be initialized to and pruned to |
| edu.vt.middleware.ldap.pool.maxPoolSize | 10                | Maximum size the pool can grow to |
| edu.vt.middleware.ldap.pool.validateOnCheckIn | false             | whether connections should be validated when objects are returned to the pool |
| edu.vt.middleware.ldap.pool.validateOnCheckOut | false             | whether connections should be validated when objects are loaned out from the pool |
| edu.vt.middleware.ldap.pool.validatePeriodically | false             | whether connections should be validated periodically when the pool is idle |
| edu.vt.middleware.ldap.pool.pruneTimerPeriod | 300000 (5 min)    | period at which pool should be pruned |
| edu.vt.middleware.ldap.pool.validateTimerPeriod | 1800000 (30 min)  | period at which pool should be validated |
| edu.vt.middleware.ldap.pool.expirationTime | 600000 (10 min)   | time an object can stay in the pool before it is considered stale and available for pruning |


---

# Implementations #

## BlockingLdapPool ##
Implements a pool of ldap objects that has a set minimum and maximum size.<br />
The pool will not grow beyond the maximum size and when the pool is exhausted, requests for new objects will block.<br />
The length of time the pool will block before throwing an exception is configurable.<br />
By default the pool will block indefinitely and there is no guarantee that waiting threads will be serviced in the order in which they made their request.<br />
This implementation should be used when you need to control the <em>exact</em> number of ldap connections that can be created.

```
DefaultLdapFactory factory = new DefaultLdapFactory(
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
BlockingLdapPool pool = new BlockingLdapPool(factory);
pool.setBlockWaitTime(5000); // wait for 5sec for an object to be available
pool.initialize();
Ldap ldap = null;
try {
  ldap = pool.checkOut();
  ...
  Iterator<SearchResult> i = ldap.search(
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});
  ...
} catch (BlockingTimeoutException e) {
  log.error("Gave up waiting on the pool", e);
} catch (PoolInterruptedException e) {
  log.error("Someone interrupted me", e);
} catch (LdapPoolException e) {
  log.error("Serious problem with the ldap, connection could not be created and the pool is empty", e);
} finally {
  pool.checkIn(ldap);
}
...
pool.close();
```

## SoftLimitLdapPool ##
Implements a pool of ldap objects that has a set minimum size and will grow as necessary based on it's current load.<br />
Pool size will return to it's minimum based on the configuration of the prune timer.<br />
This implementation should be used when you have some flexibility in the number of ldap connections that can created to handle spikes in load.<br />
Note that this pool will begin blocking if it cannot create new ldap connections.

```
DefaultLdapFactory factory = new DefaultLdapFactory(
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
SoftLimitLdapPool pool = new SoftLimitLdapPool(factory);
pool.setBlockWaitTime(5000); // wait for 5sec for an object to be available
pool.initialize();
Ldap ldap = null;
try {
  ldap = pool.checkOut();
  ...
  Iterator<SearchResult> i = ldap.search(
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});
  ...
} catch (BlockingTimeoutException e) {
  log.error("Gave up waiting on the pool", e);
} catch (PoolInterruptedException e) {
  log.error("Someone interrupted me", e);
} catch (LdapPoolException e) {
  log.error("Serious problem with the ldap, connection could not be created and the pool is empty", e);
} finally {
  pool.checkIn(ldap);
}
...
pool.close();
```

## SharedLdapPool ##
Implements a pool of ldap objects that has a set minimum and maximum size.<br />
The pool will not grow beyond the maximum size and when the pool is exhausted, requests for new objects will be serviced by objects that are already in use.<br />
Since Ldap objects are thread safe object this implementation leverages that by sharing ldap objects among requests.<br />
See http://java.sun.com/j2se/1.5.0/docs/api/javax/naming/ldap/LdapContext.html#newInstance(javax.naming.ldap.Control[]) for more information about how JNDI contexts can be used in a thread safe manner.<br />
This implementation should be used when you want some control over the maximum number of ldap connections, but can tolerate some new connections under high load.

```
DefaultLdapFactory factory = new DefaultLdapFactory(
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
SharedLdapPool pool = new SharedLdapPool(factory);
pool.initialize();
Ldap ldap = null;
try {
  ldap = pool.checkOut();
  ...
  Iterator<SearchResult> i = ldap.search(
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});
  ...
} catch (LdapPoolException e) {
  log.error("Serious problem with the ldap, connection could not be created and the pool is empty", e);
} finally {
  pool.checkIn(ldap);
}
...
pool.close();
```


---

# Activation and Passivation #
This library supports activating an object on check out and passivating it on check in.<br />
By default neither of these operations occur, so if you desire this functionality you'll need to configure your pool appropriately.<br />
The following implementations are provided:
  * ConnectLdapActivator
  * CloseLdapPassivator
Activators and Passivators should only be used if you have a specific use case for them, as in general, they will negatively affect the performance of your pool.<br />
The following example demonstrates a pool configured to connect whenever an object is retrieved from the pool and to close whenever an object is returned.<br />
This is a contrived example to demonstrate how to configure activators and passivators, as this sort of configuration will yield no performance gains from connection pooling.<br />
Note that if you are using the `DefaultLdapFactory`, it will attempt to connect ldap objects before making them available to the pool, therefore you only need to use an activator if you specifically want to attempt a connection on every check out.
```
DefaultLdapFactory factory = new DefaultLdapFactory(
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
factory.setConnectOnCreate(false);
factory.setLdapActivator(new ConnectLdapActivator());
factory.setLdapPassivator(new CloseLdapPassivator());
SoftLimitLdapPool pool = new SoftLimitLdapPool(factory);
pool.setBlockWaitTime(5000); // wait for 5sec for an object to be available
pool.initialize();
Ldap ldap = null;
try {
  ldap = pool.checkOut();
  ...
  Iterator<SearchResult> i = ldap.search(
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});
  ...
} catch (LdapActivationException e) {
  log.error("Ldap object failed activation...", e);
} catch (BlockingTimeoutException e) {
  log.error("Gave up waiting on the pool", e);
} catch (PoolInterruptedException e) {
  log.error("Someone interrupted me", e);
} catch (LdapPoolException e) {
  log.error("Serious problem with the ldap, connection could not be created and the pool is empty", e);
} finally {
  pool.checkIn(ldap);
}
...
pool.close();
```


---

# Validation #
This library supports validating an object on check out and check in.<br />
By default neither of these operations occur, so if you desire this functionality you'll need to configure your pool appropriately.<br />
The following implementations are provided:
  * ConnectLdapValidator
  * CompareLdapValidator
Validators provide an easy hook for checking whether an object is still fit to reside in the pool.<br />
Objects that fail validation are evicted from the pool.<br />
The following is an example of validating an object by using a compare operation.<br />
```
LdapPoolConfig config = new LdapPoolConfig();
config.setValidateOnCheckOut(true);
DefaultLdapFactory factory = new DefaultLdapFactory(
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
factory.setLdapValidator(
  new CompareLdapValidator(
    "ou=People,dc=vt,dc=edu", new SearchFilter("ou=People"))); // perform a simple compare
SoftLimitLdapPool pool = new SoftLimitLdapPool(config, factory);
pool.setBlockWaitTime(5000); // wait for 5sec for an object to be available
pool.initialize();
Ldap ldap = null;
try {
  ldap = pool.checkOut();
  ...
  Iterator<SearchResult> i = ldap.search(
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});
  ...
} catch (LdapValidationException e) {
  log.error("Ldap object failed compare validation...", e);
} catch (BlockingTimeoutException e) {
  log.error("Gave up waiting on the pool", e);
} catch (PoolInterruptedException e) {
  log.error("Someone interrupted me", e);
} catch (LdapPoolException e) {
  log.error("Serious problem with the ldap, connection could not be created and the pool is empty", e);
} finally {
  pool.checkIn(ldap);
}
...
pool.close();
```

## Periodic Validation ##
By default the ValidatePoolTask is always running.<br />
If no validation is configured, it will simply ensure your pool stays at it's minimum size.<br />
However, you can also configure validation to occur when the pool is idle and not during check outs and check ins.<br />
By performing validation periodically rather than for every checkIn/checkOut you will improve performance during peak periods of load.
```
LdapPoolConfig config = new LdapPoolConfig();
config.setValidatePeriodically(true); // by default validate the pool every 30 min, if idle
DefaultLdapFactory factory = new DefaultLdapFactory(
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
factory.setLdapValidator(
  new CompareLdapValidator(
    "ou=People,dc=vt,dc=edu", new SearchFilter("ou=People"))); // perform a simple compare
SoftLimitLdapPool pool = new SoftLimitLdapPool(config, factory);
pool.initialize();
Ldap ldap = null;
try {
  ldap = pool.checkOut();
  ...
  Iterator<SearchResult> i = ldap.search(
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});
  ...
} catch (LdapPoolException e) {
  log.error("Problems using the pool, see your error log...", e);
} finally {
  pool.checkIn(ldap);
}
...
pool.close();
```