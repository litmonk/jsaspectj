# jsaspectj
类方法环绕切面框架，在类加载之前动态修改类方法，实现对类方法的环绕切面以嵌入自定义逻辑。

package: 
    mvn clean package -DskipTests
	
test:
    mvn test -Dtest=ClassMethodProxyTest
