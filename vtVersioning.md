# Versioning Policy #

## Prior to Release ##
During day-to-day development, such as what occurs prior to a release, the version number should contain the maven snapshot identifier to indicate that the project is currently in a snapshot state.  The base version number should be for the _next_ pending release version.  The following example demonstrates the `vt-crypt/trunk/pom.xml` during active development toward the 2.0.1 release:

```
<project>
...
  <artifactId>vt-crypt</artifactId>
  <packaging>jar</packaging>
  <version>2.0.1-SNAPSHOT</version>
...
</project>
```

## Release Versioning ##
When a release version is ready to be tagged according to the [release procedures](vtReleaseProcedures.md), the snapshot identifier is simply truncated from the version and **immdiately committed** _prior_ to tagging the final release.  The following example demonstrates the `vt-crypt/trunk/pom.xml` immediately before tagging the `2.0.1` release in the `tags` branch.
```
<project>
...
  <artifactId>vt-crypt</artifactId>
  <packaging>jar</packaging>
  <version>2.0.1</version>
...
</project>
```

## Post Release ##
The next time _any_ change is committed to trunk after a release, the version number should be incremented to the next point number version, e.g. `2.0.1 -> 2.0.2`, and designated a snapshot.  The only exception to this rule would be for situations where substantial changes are planned for the next release such that it would merit a minor or major number change.  In any case the POM should contain a snapshot designation until the next release is cut from trunk.  The following example demonstrates the state of `vt-crypt/trunk/pom.xml` upon commiting the first change after the `2.0.1` release.
```
<project>
...
  <artifactId>vt-crypt</artifactId>
  <packaging>jar</packaging>
  <version>2.0.2-SNAPSHOT</version>
...
</project>
```

# Versioning Numbers #
All projects should follow a numbering scheme of:
```
major.minor.revision
```
The version associated with a project should generally follow the following guidelines:
  * The initial non-beta release should be 1.0. Until that time the major number should be 0.
  * Major version numbers should be bumped for large refactoring of the code base.
    * Moving to a newer version of the JDK may also apply
  * Minor version numbers should be bumped for destructive interface changes and new functionality.
    * Any change that has the potential to break a client using the current minor version should use the next minor version.
    * Any change that provides new functionality such that it should be distinguished from previous releases.
  * Revision version numbers should be bumped for all other changes.