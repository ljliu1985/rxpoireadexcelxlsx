Introduction
============
Android Poi Import/Export Excel Support xlsx
使用Poi读取高版本Excel xlsx文件，RxJava处理，包含进度信息，基于注解生成对象，数据校验。基于注解导出到excel文件,支持U盘操作


Screenshots
===========
<img src="https://github.com/ljliu1985/rxpoireadexcel/blob/master/device-2018-08-24-194143.png">
<img src="https://github.com/ljliu1985/rxpoireadexcel/blob/master/device-2018-08-24-195217.png">

project gradle:
```groovy
maven { url 'https://jitpack.io' }

maven { url 'https://dl.bintray.com/magnusja/maven' }
```

app gradle:
```groovy
implementation  'com.github.ljliu1985:rxpoireadexcelxlsx:1.3'
 ```

这里主要是rt.jar包解决解释xlsx的两个错误：
java.lang.NoClassDefFoundError: Failed resolution of: Ljavax/xml/stream/XMLEventFactory; at org.apache.poi.openxml4j.opc.internal.marshallers.PackagePropertiesMarshaller.(PackagePropertiesMarshaller.java:41) at org.apache.poi.openxml4j.opc.OPCPackage.init(OPCPackage.java:161) at org.apache.poi.openxml4j.opc.OPCPackage.(OPCPackage.java:141) at org.apache.poi.openxml4j.opc.ZipPackage.(ZipPackage.java:97) at org.apache.poi.openxml4j.opc.OPCPackage.open(OPCPackage.java:324) at org.apache.poi.ss.usermodel.WorkbookFactory.create(WorkbookFactory.java:184) at org.apache.poi.ss.usermodel.WorkbookFactory.create(WorkbookFactory.java:149)

javax.xml.stream.FactoryConfigurationError: Provider com.sun.xml.internal.stream.events.XMLEventFactoryImpl not found at javax.xml.stream.FactoryFinder.newInstance(Unknown Source) at javax.xml.stream.FactoryFinder.newInstance(Unknown Source) at javax.xml.stream.FactoryFinder.find(Unknown Source) at javax.xml.stream.FactoryFinder.find(Unknown Source) at javax.xml.stream.XMLEventFactory.newInstance(Unknown Source) at org.apache.poi.openxml4j.opc.internal.marshallers.PackagePropertiesMarshaller.(PackagePropertiesMarshaller.java:41) at org.apache.poi.openxml4j.opc.OPCPackage.init(OPCPackage.java:161) at org.apache.poi.openxml4j.opc.OPCPackage.(OPCPackage.java:141) at org.apache.poi.openxml4j.opc.ZipPackage.(ZipPackage.java:97) at org.apache.poi.openxml4j.opc.OPCPackage.open(OPCPackage.java:324) at org.apache.poi.ss.usermodel.WorkbookFactory.create(WorkbookFactory.java:184) at org.apache.poi.ss.usermodel.WorkbookFactory.create(WorkbookFactory.java:149)

