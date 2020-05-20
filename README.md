# HamsterAPI
叁只仓鼠个人使用的API

# 现已支持构建工具
## Maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```xml
	<dependency>
	    <groupId>com.github.MiniDay</groupId>
	    <artifactId>HamsterAPI</artifactId>
	    <version>2.2.0</version>
        <scope>provided</scope>
	</dependency>
```
## Gradle
```gradle
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
```
```gradle
	dependencies {
	        compileOnly 'com.github.MiniDay:HamsterAPI:2.2.0'
	}
```

## API使用方法
请参考[HamsterAPI.java](https://github.com/ViosinDeng/HamsterAPI/blob/master/src/main/java/cn/hamster3/api/HamsterAPI.java)
