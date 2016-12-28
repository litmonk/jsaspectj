package com.litmonk.jsaspectj;

import javassist.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类方法代理（环绕切面）
 * Created by lu on 2016/12/22.
 */
public class ClassMethodProxy {
    /**
     * 动态代理方法的方法名后缀
     */
    private final static String PROXY_CLASS_NAME_SUFFIX = "$LuProxy";

    /**
     * 保存被代理方法对象的变量名
     */
    private final static String PROXY_METHOD_VARIABLE = "_LuProxyMethod";

    /**
     * 环绕拦截器接口
     */
    private final static String AROUND_ASPECTJ_HANDLER_INTERFACE = "com.litmonk.jsaspectj.IAroundAspectJHandler";

    private static Map<String, ProxyBean> proxyBeanMap = null;

    /**
     * 要加载的类是否被配置了方法代理，如果是则进行相应代理出来
     * 可配合类加载器进行使用，适用于类加载器与功能代码不在一起的场景，例如tomcat中
     *
     * @param proxyClass 要加载的类名，包含路径，eg: com.litmonk.ProxyTest
     * @return 被处理过的class
     */
    public static Class proxyClassMethod(String proxyClass) {
        if (proxyBeanMap == null) {
            proxyBeanMap = new HashMap<String, ProxyBean>();
            ProxyClassFactory.init();
            List<ProxyBean> proxyBeenList = ProxyClassFactory.getProxyBeanList();
            if (proxyBeenList != null) {
                for (ProxyBean proxyBean : proxyBeenList) {
                    proxyBeanMap.put(proxyBean.getProxyClass(), proxyBean);
                }
            }
        }

        ProxyBean proxyBean = proxyBeanMap.get(proxyClass);
        if (proxyBean != null) {
            try {
                return proxyClassMethod(proxyBean.getProxyClass(), proxyBean.getMethodName(),
                        proxyBean.getAroundAspectJHandlerImpl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 类方法代理处理
     * @param proxyClass 要加载的类名，包含路径，eg: com.litmonk.ProxyTest
     * @param methodName 要被代理的方法名
     * @param aroundAspectJHandlerImplClass 切面处理实现类，继承自IAroundAspectJHandler
     * @return 被处理过的class 切面处理实现类，继承自IAroundAspectJHandler
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    public static Class proxyClassMethod(String proxyClass, String methodName, Class aroundAspectJHandlerImplClass)
            throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

        CtClass ctProxyClass = classPool.get(proxyClass);
        CtMethod ctProxyMethod = ctProxyClass.getDeclaredMethod(methodName);

        aroundAspectJMethod(proxyClass, ctProxyClass, ctProxyMethod, aroundAspectJHandlerImplClass.getName());

        return ctProxyClass.toClass();
    }

    /**
     * 修改类实现，添加环绕切面方法
     * @param proxyClass 要加载的类名，包含路径，eg: com.litmonk.ProxyTest
     * @param ctProxyClass 要加载的类
     * @param ctProxyMethod 要被代理的方法
     * @param aroundAspectJHandlerImpl
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private static void aroundAspectJMethod(String proxyClass, CtClass ctProxyClass, CtMethod ctProxyMethod, String aroundAspectJHandlerImpl)
            throws NotFoundException, CannotCompileException {
        String methodCode = generateMethodCode(proxyClass, ctProxyMethod, aroundAspectJHandlerImpl);

        ctProxyMethod.setModifiers(Modifier.PUBLIC);
        ctProxyMethod.setName(ctProxyMethod.getName() + PROXY_CLASS_NAME_SUFFIX);

        CtField ctField = CtField.make("private static java.lang.reflect.Method " + PROXY_METHOD_VARIABLE + " = null;", ctProxyClass);
        ctProxyClass.addField(ctField);

        CtMethod cm = CtNewMethod.make(methodCode, ctProxyClass);
        ctProxyClass.addMethod(cm);
    }

    /**
     * 生产环绕切面方法
     * @param proxyClass  要加载的类名，包含路径，eg: com.litmonk.ProxyTest
     * @param ctProxyMethod 要被代理的方法
     * @param aroundAspectJHandlerImpl 切面处理实现类，继承自IAroundAspectJHandler
     * @return
     * @throws NotFoundException
     */
    private static String generateMethodCode(String proxyClass, CtMethod ctProxyMethod, String aroundAspectJHandlerImpl) throws NotFoundException {
        String methodName = ctProxyMethod.getName();
        CtClass[] parameterTypes = ctProxyMethod.getParameterTypes();
        CtClass[] exceptionTypes = ctProxyMethod.getExceptionTypes();

        //组装方法的Exception声明
        StringBuilder exceptionBuilder = new StringBuilder();
        if (exceptionTypes.length > 0) {
            exceptionBuilder.append(" throws ");
            for (int i = 0; i < exceptionTypes.length; i++) {
                if (i != exceptionTypes.length - 1) {
                    exceptionBuilder.append(exceptionTypes[i].getName()).append(",");
                } else {
                    exceptionBuilder.append(exceptionTypes[i].getName());
                }
            }
        }

        StringBuilder methodBodyBuilder = new StringBuilder(" {\n");
        methodBodyBuilder.append("Object returnObject = null;\n");
        methodBodyBuilder.append(AROUND_ASPECTJ_HANDLER_INTERFACE).append(" aroundAspectJHandler = new ").append(aroundAspectJHandlerImpl).append("();\n");
        methodBodyBuilder.append("if (").append(PROXY_METHOD_VARIABLE).append(" == null) {\n");
        methodBodyBuilder.append("java.lang.reflect.Method[] methods = Class.forName(\"").append(proxyClass).append("\").getMethods();\n");
        methodBodyBuilder.append("for (int i = 0; i < methods.length; i++) {\njava.lang.reflect.Method method = methods[i]; \nif (\"");
        methodBodyBuilder.append(methodName).append(PROXY_CLASS_NAME_SUFFIX).append("\".equals(method.getName())) {\n");
        methodBodyBuilder.append(PROXY_METHOD_VARIABLE).append(" = method; break;\n}\n}\n}\n");
        methodBodyBuilder.append("returnObject = aroundAspectJHandler.invoke(this, ").append(PROXY_METHOD_VARIABLE).append(", ");

        //传递方法里的参数+组装方法的参数列表
        StringBuilder parameterBuilder = new StringBuilder();
        if (parameterTypes.length > 0) {
            methodBodyBuilder.append("new Object[]{");
            for (int i = 0; i < parameterTypes.length; i++) {
                String varName = "var" + i;
                if (i != parameterTypes.length - 1) {
                    methodBodyBuilder.append("($w)").append(varName).append(",");
                    parameterBuilder.append(parameterTypes[i].getName()).append(" ").append(varName).append(",");
                } else {
                    methodBodyBuilder.append("($w)").append(varName);
                    parameterBuilder.append(parameterTypes[i].getName()).append(" ").append(varName);
                }
            }
            methodBodyBuilder.append("});\n");
        } else {
            methodBodyBuilder.append("null);\n");
        }

        methodBodyBuilder.append(getReturnExpression(ctProxyMethod.getReturnType()));
        methodBodyBuilder.append("}");

        StringBuilder methodBuilder = new StringBuilder(getModifiersString(ctProxyMethod.getModifiers()));
        methodBuilder.append(" ").append(ctProxyMethod.getReturnType().getName()).append(" ").append(methodName).append("(")
                .append(parameterBuilder).append(")").append(exceptionBuilder).append(methodBodyBuilder);

        //System.out.println(methodBuilder.toString());
        return methodBuilder.toString();
    }

    private static String getReturnExpression(CtClass returnType) {
        if (returnType.isPrimitive()) {
            if (returnType.equals(Boolean.TYPE)) return "return ((Boolean)returnObject).booleanValue();\n";
            if (returnType.equals(Integer.TYPE)) return "return ((Integer)returnObject).intValue();\n";
            if (returnType.equals(Long.TYPE)) return "return ((Long)returnObject).longValue();\n";
            if (returnType.equals(Float.TYPE)) return "return ((Float)returnObject).floatValue();\n";
            if (returnType.equals(Double.TYPE)) return "return ((Double)returnObject).doubleValue();\n";
            if (returnType.equals(Character.TYPE)) return "return ((Character)returnObject).charValue();\n";
            if (returnType.equals(Byte.TYPE)) return "return ((Byte)returnObject).byteValue();\n";
            if (returnType.equals(Short.TYPE)) return "return ((Short)returnObject).shortValue();\n";
        } else {
            return "return (" + returnType.getName() + ")returnObject;\n";
        }

        return "";
    }

    private static String getModifiersString(int modifiers) {
        switch (modifiers) {
            case Modifier.PUBLIC:
                return "public";
            case Modifier.PROTECTED:
                return "protected";
            case Modifier.PRIVATE:
                return "private";
            default:
                return "";
        }
    }
}
