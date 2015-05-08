Arquillian Chameleon Container
==============================

> Proxy Container for all JBoss AS / JBoss EAP / WildFly containers

Have you ever faced an issue that switching between container implementations isn't that easy?

Testing against several containers (JBoss AS / JBoss EAP / WildFly) or even switching between different modes (Managed, Remote, Embedded) may result in bloated `pom.xml`.


Chameleon Containers are able to quickly adopt to your needs without too much hassle.

Story
-----

> _Chameleons are a tall, lizard-looking alien race that has (same as their earthling friends) ability to change colours when adopting to various environments. They are usually transported in spaceships called Containers._


Get Started
-------------

Do whatever you [would do normally](http://arquillian.org/guides/getting_started/) and add Chameleon Container instead of any application-server specific stuff:

````
<dependency>
    <groupId>org.jboss.arquillian.junit</groupId>
    <artifactId>arquillian-junit-container</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.arquillian.container</groupId>
    <artifactId>arquillian-container-chameleon</artifactId>
    <version>1.0.0.Final-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
````

Add a sauce into `arquillian.xml`:

````
<defaultProtocol type="Servlet 3.0"></defaultProtocol>

<container qualifier="proxy" default="true">
    <configuration>
        <property name="target">wildfly:8.0.0.Final:managed</property>
        <property name="serverConfig">standalone-full.xml</property>
    </configuration>
</container>
````

Now, you can switch container implementation as simple as changing target (e.g. `wildfly:8.0.0.Final:remote` or `wildfly:9.0.0.CR1:managed`).

And off course, you can still configure the underlying container by its specific configuration (e.g. change `serverConfig`).


Supported Containers
--------------------

* JBoss EAP
  * 6.x
* WildFly
  * 9.x
  * 8.x
* JBoss AS
  * 7.x


Community
---------

* Chat: #arquillian channel @ [irc.freenode.net](http://webchat.freenode.net/)
* [Blogs](http://arquillian.org/blog/)
* [Forums](http://discuss.arquillian.org/)
