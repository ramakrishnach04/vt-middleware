# Release Procedures #
  1. Verify if any serializable classes need their serialVersionUid updated
  1. Run jalopy source clean up on both the main and test source trees. Commit any changes.
  1. Verify that javadocs build without any warnings
  1. Update any info related files, such as, README, LICENSE, NOTICE, etc.
  1. Verify the maven package command succeeds in trunk.
```
  mvn package
```
  1. Assuming the project adheres to the [versioning policy](vtVersioning.md), increment the version number in the project POM and relevant module POMs to the final release version number, VERSION, and commit the change:
```
  vi pom.xml MODULEA/pom.xml MODULEB/pom.xml
  svn commit -m"Upgrade POM version numbers to final release version."
```
  1. Tag your release:
```
  svn cp $GSVN/project-name/trunk $GSVN/project-name/tags/project-name-VERSION
```
  1. Check out the tag
```
  svn co $GSVN/project-name/tags/project-name-VERSION
```
  1. Build the package from the tagged source
```
  mvn -Dsign=true repository:bundle-create
```
    * When prompted for which files to exclude, make selections so that your bundle only includes classes jar, sources jar, javadoc jar, and their associated signatures.
  1. Upload the dist files to the google code site
```
  http://code.google.com/p/vt-middleware/downloads/list
```
  1. Commit the javadocs
```
    svn mkdir $GSVN/project-name/javadoc/project-name-VERSION
    unzip target/project-name-VERSION-javadoc.jar .
    rm -rf META-INF project-name-VERSION-javadoc.jar
    svn commit javadoc/project-name-VERSION
```
    * Mime types may need to be set if your svn config is not set to do so.  It is necessary to specify mime types for files used in the javadocs in order for them to render properly in a browser.
```
      find ./ -type f -name "*.html" -exec svn propset svn:mime-type text/html '{}' \;
      find ./ -type f -name "*.css" -exec svn propset svn:mime-type text/css '{}' \;
      find ./ -type f -name "*.gif" -exec svn propset svn:mime-type image/gif '{}' \;
```
  1. Update the release notes on the wiki and any other documentation that requires changes.
## Google Code Maven Deployment ##
  1. Deploy the release jar to the maven repo (requires maven >= 2.1.0)
```
  mvn deploy:deploy-file \
    -DpomFile=pom.xml \
    -Dfile=target/project-name-VERSION.jar \
    -Durl=svn:https://vt-middleware.googlecode.com/svn/maven2 \
    -DrepositoryId=vt-middleware.googlecode.com.repo
```
  1. Deploy the source jar to the maven repo (requires maven >= 2.1.0)
```
  mvn deploy:deploy-file \
    -DgroupId=edu.vt.middleware \
    -DartifactId=project-name \
    -Dversion=VERSION \
    -DgeneratePom=false \
    -Dpackaging=java-source \
    -Dfile=target/project-name-VERSION-sources.jar \
    -Durl=svn:https://vt-middleware.googlecode.com/svn/maven2 \
    -DrepositoryId=vt-middleware.googlecode.com.repo
```
## Sonatype Maven Deployment ##
_For complete Sonatype instructions see:_ [Sonatype OSS Maven Repository Usage Guide](https://docs.sonatype.org/display/repository/sonatype+oss+maven+repository+usage+guide)

  1. Login to the [Nexus OSS instance](https://oss.sonatype.org)
  1. Select 'Staging Upload', on the left panel
  1. Select 'Upload Mode' -> 'Artifact Bundle', in the center panel
  1. Click 'Select Bundle to Upload...', select the bundle jar created in step #8, and click 'Upload Bundle'
    * You will immediately receive feedback on whether the bundle meets Sonatype artifact standards
  1. Select 'Staging Repositories' on the left panel
  1. Select the newly created repository and click 'Release'
    * You will be notified when your project has been synced to Maven Central