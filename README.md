# HtmlParser

## 一、子链接的提取：
### HtmlLinkParser.extractLinks
#### 做页面子链接提取的基本思路是：
1. 用被提取的网页的url实例化一个Parser
2. 实例化Filter，设置页面过滤条件——只获取a标签与frame标签的内容
3. 用Parser提取页面中所有通过Filter的结点，得到NodeList
4. 遍历NodeList，调用Node的相应方法得到其中的链接，加入子链接的集合
5. 返回子链接集合

## 二、解析网页内容：
### 
#### 基本思路：
1. 读取html文件，获得页面编码，获得String格式的文件内容
2. 用页面编码实例化html文件的Parser
3. 对需要提取的结点设置相应的Filter
4. 根据给定的Filter，用Parser解析html文件
5. 根据给定的Filter，用Parser解析html文件

## 读者需要注意两点：
1. 用BufferedReader读取文件是需要编码方式的，但是第一次读取我们必然不知道网页的编码。好在网页对于编码的描述在html语言框架中，我们用默认的编码方式读取文件就可以获取编码。但这个读取的文件的文本内容可能因为编码不正确而产生乱码，所以得到编码后，我们应使用得到的编码再实例化一个BufferedReader读取文件，这样得到的文件就是正确的了（除非网页本身给的编码就不对）。
获得正确的编码对于解析网页内容是非常重要的，而网络上什么样的网页都有，我推荐使用比较基础、可靠的方法获得编码，我使用的是正则匹配。
举个例子：
这是http://kb.cnblogs.com/page/143965/的对编码的描述：
```
<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
```
这是http://www.ucsd.edu/的对编码的描述：
```
<meta charset="utf-8"/>
```

2. 不熟悉html的读者可能有所不知<meta>的作用，来看看博客园首页的源码：
```
<meta name="keywords" content="博客园,开发者,程序员,软件开发,编程,代码,极客,Developer,Programmer,Coder,Code,Coding,Geek,IT学习"/><meta name="description" content="博客园是面向程序员的高品质IT技术学习社区，是程序员学习成长的地方。博客园致力于为程序员打造一个优秀的互联网平台，帮助程序员学好IT技术，更好地用技术改变世界。" />
```
这两类<meta>标签的很好的描述了网页的内容

3. 由于网页的正文通常是一段最长的纯文本内容，所以当我们得到一个p,li,ul标签的纯文本后，我们可以通过判断字符串的长度来得到网页的正文。
对页面大量的信息进行处理是很费时的，页面的<title>标签和<meta>标签中往往有对网页内容最精炼的描述，开发者应该考虑性能与代价

