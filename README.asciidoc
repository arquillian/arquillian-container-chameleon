== Arquillian Chameleon Container image:https://travis-ci.org/arquillian/arquillian-container-chameleon.svg?branch=master["Build Status", link="https://travis-ci.org/arquillian/arquillian-container-chameleon"]


[quote]
Proxy Container for all JBoss AS / JBoss EAP / WildFly containers

Have you ever faced an issue that switching between container implementations isn't that easy?

Testing against several containers (JBoss AS / JBoss EAP / WildFly) or even switching between different modes (Managed, Remote, Embedded) may result in bloated `pom.xml`.


Chameleon Containers are able to quickly adopt to your needs without too much hassle.


==== Story

[quote]
Chameleons are a tall, lizard-looking alien race that has (same as their earthling friends) ability to change colours when adopting to various environments. They are usually transported in spaceships called Containers.


=== Get Started

Do whatever you http://arquillian.org/guides/getting_started/[would do normally] and add Chameleon JUnit or TestNG Container starters instead of any application-server specific artifact:

[source, xml]
.arquillian-chameleon-junit-container-starter
----
<dependency>
    <groupId>org.arquillian.container</groupId>
    <artifactId>arquillian-chameleon-junit-container-starter</artifactId>
    <version>1.0.0.Final-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
----

[source, xml]
.arquillian-chameleon-testng-container-starter
----
<dependency>
    <groupId>org.arquillian.container</groupId>
    <artifactId>arquillian-chameleon-testng-container-starter</artifactId>
    <version>1.0.0.Final-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
----

Add this configuration to `arquillian.xml`:

[source, xml]
----
<container qualifier="chameleon" default="true">
    <configuration>
        <property name="chameleonTarget">wildfly:8.0.0.Final:managed</property>
        <property name="serverConfig">standalone-full.xml</property>
    </configuration>
</container>
----

Now, you can switch container implementation as simple as changing the chameleonTarget configuration option(e.g. `wildfly:8.0.0.Final:remote` or `wildfly:9.0.0.CR1:managed`).

And off course, you can still configure the underlying container by its specific configuration (e.g. change `serverConfig`).

Example chameleonTarget values:

* jboss eap:6.2:remote
* jboss as:7.1.1.Final:managed
* wildfly:9.0.0.Alpha1:embedded

=== Supported Containers

* JBoss EAP
** 6.x
** 7.x
* WildFly
** 15.x
** 14.x
** 13.x
** 12.x
** 11.x
** 10.x
** 9.x
** 8.x
* JBoss AS
** 7.0.*
** 7.1.0.* to 7.1.1.* (version > 7.1.1.* are not supported.)
* GlassFish
** 3.1.2.*
** 4.1.* (versions prior to 4.1.x are no longer supported)
** 5.x
* Payara
** 4.x
** 5.x
* Tomcat
** 8.0.*
** 7.x
** 6.x
* Tomee
** 7.x (embedded mode not supported)
** 8.x (embedded mode not supported)

[NOTE]
Chameleon will download and extract the target container if no distribution home is configured and target type is either embedded or managed.

[IMPORTANT]
Glassfish versions prior to 3.1.2 are no longer supported due to issues with dependencies of the distribution

=== Configuration options

==== chameleonTarget

Define the underlying target in the format name:version[:type]. If no _type_ is defined, the adapter type used depend on the underlying
container configuration. Use no type if you just want 'something' up and running with as little extra configuration as possible.

==== chameleonDistributionDownloadFolder

Override the default download folder for container distributions. Could be defined as `TMP` to create a custom folder under `java.io.tmpdir`, else
it will be read as a directory. Defaults to detect current build system, either `target/` for Maven or `build/` for Gradle.

==== chameleonContainerConfigurationFile

Define the container configuration file to use. Defaults to the containers.yaml file provided by Chameleon.

==== chameleonResolveCacheFolder

Define where Chameleon should store the resolver cache files. By default it will use `'chameleonDistributionDownloadFolder'/cache`.

== Development

If you want to add your own container configurations or contribute to the ones shipped as default with Chameleon you can
use the following format to describe them:

[source,yaml]
.chameleon/default/containers.yaml
----
- name: WildFly <1>
  versionExpression: 10.*  <2>
  adapters: <3>
    - type: remote <4>
      coordinates: org.wildfly.arquillian:wildfly-arquillian-container-remote:1.0.0.Final <5>
      adapterClass: org.jboss.as.arquillian.container.remote.RemoteDeployableContainer <6>
    - type: managed
      coordinates: org.wildfly.arquillian:wildfly-arquillian-container-managed:1.0.0.Final
      adapterClass: org.jboss.as.arquillian.container.managed.ManagedDeployableContainer
      configuration: <8>
        jbossHome: ${dist} <9>
    - type: embedded <10>
      coordinates: org.wildfly.arquillian:wildfly-arquillian-container-embedded:${version} <11>
      adapterClass: org.jboss.as.arquillian.container.embedded.EmbeddedDeployableContainer
      requireDist: false <12>
      dependencies: <13>
        - org.glassfish.extras:glassfish-embedded-all:${version} <14>
  defaultType: managed <15>
  dist: <16>
    coordinates: org.wildfly:wildfly-dist:zip:${version} <17>
  defaultProtocol: Servlet 3.0 <18>
  exclude: <19>
    - org.jboss.arquillian.test:* <20>
    - org.jboss.arquillian.testenricher:*
    - "*:wildfly-arquillian-testenricher-msc"
----
<1> *required* The _name_ section of the _chameleonTarget_.
<2> *required* A Regular Expression to match against the _version_ section of the _chamleonTarget_ to activate this configuration.
<3> *required* A list of Adapters supported by this _name_ and _version_ combination.
<4> *required* The Adapter that match the _type_ section of the _chameleonTarget_.
<5> *required* The Adapter artifacts _GAV_ so it can be downloaded from a repository.
<6> *required* The Adapter _DeployableContainer_ implementation class to invoke.
<7> The Adapter that match the _type_ section of the _chameleonTarget_.
<8> *optional* List of Adapter configuration option that will be automatically activated if not present from user.
<9> ${dist} special variable that is replaced with the location of the downloaded/extracted distribution if applicable.
<10> The Adapter that match the _type_ section of the _chameleonTarget_.
<11> ${version} special variable that is replaced with the _version_ section of the _chameleonTarget_ as provided by the user.
<12> *optional* Flag to turn off default automatic download of distribution if not required by the Adapter. e.g. GlassFish Embedded requires no extracted distribution to run.
<13> *optional* List of additional dependencies required by the Adapter.
<14> The dependency _GAV_.
<15> *optional* Describes which adapter to select if no _type_ section is defined in the _chameleonTarget_.
<16> *optional* Section to describe how to download the distribution.
<17> The distribution artifact _GAV_ so it can be downloaded from a repository.
<18> *optional* Override the Adapters _defaultProtocol_ as described by the _DeployableContainer_ implementation.
<19> *optional* List of dependencies to exclude when resolving the adapter _GAV_.
<20> The dependency _GAV_ expression to exclude.

NOTE: If you want to help improve the configurations, you can find issues related to this configuration labeled as https://github.com/arquillian/arquillian-container-chameleon/labels/container[container]
in the https://github.com/arquillian/arquillian-container-chameleon/issues[issue tracker].

==== WildFly Embedded
If you want to run any of the versions of WildFly embedded, you need to add an additional dependency to your `pom.xml` file:
[source,xml]
----
<dependency>
    <groupId>org.jboss.logmanager</groupId>
    <artifactId>jboss-logmanager</artifactId>
    <version>${jboss.logmanager.version}</version>
</dependency>
----
and set `java.util.logging.manager` variable to `org.jboss.logmanager.LogManager` using `maven-surefire-plugin`:
[source,xml]
----
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <java.util.logging.manager>
                org.jboss.logmanager.LogManager
            </java.util.logging.manager>
        </systemPropertyVariables>
    </configuration>
</plugin>
----

== Custom Maven setting

In case you need to specify your custom `settings.xml` file and you cannot put it at the default location (`$HOME/.m2/settings.xml`) then use the property
`org.apache.maven.user-settings`
to specify a user `settings.xml` file or
`org.apache.maven.global-settings`
to specify a global `settings.xml` file.

The standard Maven property `-s` doesn't work as Chameleon internally uses Shrinkwrap Resolver and the property is not supported there. But you can use any of the properties described here: https://github.com/shrinkwrap/resolver#system-properties

== Arquillian Chameleon Runner

Arquillian Chameleon Container is a special container that allows you to define which container and mode without having to remember any concrete dependency of the desired container.
You've seen this at <<Get Started>>.

This approach is the most versatile one and has been here for a long time and offers a generic and global solution, but with Chameleon, you can use another approach where instead of configuring container using `arquillian.xml`, you can use an annotation to set up the test container.

The first thing to do is add next dependency:

[source, xml]
.pom.xml
----
<dependency>
    <groupId>org.arquillian.container</groupId>
    <artifactId>arquillian-container-chameleon-runner</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
----

Then instead of using Arquillian runner, you need to use a new one provided by Chameleon called `ArquillianChameleon`.

Then you need to annotate your test with `@ChameleonTarget("wildfly:9.0.0.Final:managed") where you set the container, version, and mode as you usually do with `chameleonTarget` in `arquillian.xml`.

But this annotation also allows you to set each of the property (even custom properties) one by one, for example:

[source, java]
----
@ChameleonTarget(container = "tomcat", version = "7.0.0", customProperties = {
    @Property(name="a", value="b")
})
----

Last important thing to take into consideration is that `@ChameleonTarget` can be used in meta-annotations and inherit properties form meta-annotations.
For example, you can use next form to define `Tomcat` container:

[source, java]
.Tomcat.java
----
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ChameleonTarget("tomcat:7.0.0:managed") // <1>
public @interface Tomcat {
}
----
<1> Defines container, version and mode

And then to define that the test needs to be run in `Tomcat`, you can simply do:

[source, java]
.Tomcat.java
----
@Tomcat
public class TomcatTest {
}
----

But you can even redefine meta-annotations, for example, to specify Tomcat 8 you only need to do:

[source, java]
.Tomcat8.java
----
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Tomcat // <1>
@ChameleonTarget(version = "8.0.0") // <2>
public @interface Tomcat8 {
}
----
<1> Inherit properties from `Tomcat` meta-annotation
<2> Override version number

All fields accept expressions like `${property:defaultValue} where property is first resolved as environment variable, if not set as the system property and if not the default value is used.

[IMPORTANT]
====
There are some limitations when using this approach.

* The first one is that test execution that occurs in the same JVM must use the same container, you cannot run in the same JVM a set of tests that require different containers (i.e some with Wildfly and others with Payara).
If you want to do this you need to isolate each of the tests in different JVMs.

* The second one is that if you are configuring extensions with `arquillian.properties` *AND* `arquillian.xml files at the same time and you run tests in parallel *within* the same JVM, then you might find some unexpected results.
Of course, this is a corner case, but a solution to this is just moving configuration of one of the files to either `arquillian.properties` or `arquillian.xml` file or run parallel tests in different JVMs.
====


== Test

To run the whole test suite with the correct configuration use profile `all`:

`mvn clean verify -Pall`

To run Arquillian Container TCK test suite use profile `tck`:

`mvn clean verify -Ptck`

== Community

* Chat: #arquillian channel @ http://webchat.freenode.net/[irc.freenode.net]
* http://arquillian.org/blog/[Blogs]
* http://discuss.arquillian.org/[Forums]
