# HamsterAPI
叁只仓鼠个人使用的API

# 现已支持构建工具
[![](https://jitpack.io/v/cn.hamster3/HamsterAPI.svg)](https://jitpack.io/#cn.hamster3/HamsterAPI)
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
<dependencies>
    <dependency>
        <groupId>cn.hamster3</groupId>
        <artifactId>HamsterAPI</artifactId>
        <version>2.3.11-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
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
	        compileOnly 'cn.hamster3:HamsterAPI:2.3.11-SNAPSHOT'
	}
```

## API使用方法
请参考[HamsterAPI.java](https://github.com/ViosinDeng/HamsterAPI/blob/master/src/main/java/cn/hamster3/api/HamsterAPI.java)
