package io.unfish;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.*;

public class TDF2 {
    public Map<String, String> data = new HashMap<>();
    List<String> lines = new ArrayList<>();
    Map<String, Integer> groupBegins = new HashMap<>();
    Map<String, Integer> groupEnds = new HashMap<>();
    Map<String, Integer> pathsIndices = new HashMap<>();
    Map<String, Integer> levels = new HashMap<>();
    File f = null;
    public void read(String str){
        if(str.isBlank())return;
        int currentLevel = -1;
        String groups = "";
        int lnIndex = 0;
        for(String ln : str.split("\n")){
            lines.add(ln);
            lnIndex++;
            int level = 0;
            for(int i = 0 ; i < ln.length();++i){
                char ch = ln.charAt(i);
                if(ch != ' ')break;
                level++;
            }
            if(currentLevel != -1 && level <= currentLevel){
                int difference = currentLevel - level + 1;
                for(int i = 0; i < difference; ++i){
                    if(!groups.isEmpty()) {
                        groupEnds.put(groups, lnIndex);
                        if(groups.contains(".")){
                            groups = groups.substring(0,groups.lastIndexOf('.'));
                        }else groups = "";
                    }
                    else {
                        currentLevel = -1;
                    }
                }
                if(currentLevel != -1) {
                    currentLevel = level;
                }
            }
            int x = ln.indexOf('#'), y = ln.lastIndexOf('#');
            if(x != -1) {
                if (x == y) ln = ln.substring(0, x);
                else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < ln.length(); ++i) {
                        char ch = ln.charAt(i);
                        if (i < x || i > y) {
                            sb.append(ch);
                        }
                    }
                    ln = sb.toString();
                }
            }
            ln = ln.trim();
            if(!ln.isEmpty()){
                if(ln.charAt(0) == '['){
                    currentLevel = level;
                    groups += (groups.isEmpty() ? "" : ".") + ln.substring(1,ln.length()-1);
                    groupBegins.put(groups,lnIndex);
                    levels.put(groups, level);
                }
                else if(ln.contains(":")){
                    String[] splits = ln.split(":");
                    String key = splits[0].trim(), value = splits[1].trim();
                    String path = groups.isEmpty() ? key : (groups + "." + key);
                    data.put(path,value);
                    levels.put(path,level);
                    pathsIndices.put(path,lnIndex);
                }
                else{
                    String path = groups.isEmpty() ? "global" : groups;
                    StringBuilder value = new StringBuilder();
                    if(data.containsKey(path))
                        value.append(data.get(path));
                    value.append(ln).append('\n');
                    data.put(path,value.toString());
                }
            }
        }
        while(!groups.isEmpty()){
            groupEnds.put(groups, lnIndex+1);
            if(!groups.contains("."))break;
            groups = groups.substring(0,groups.lastIndexOf('.'));
        }
    }
    public void readFromFile(File f){
        this.f = f;
        StringBuilder str = new StringBuilder();
        try{
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                str.append(sc.nextLine());
                if(sc.hasNextLine())str.append("\n");
            }
            sc.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        read(str.toString());
    }
    public void set(String path, String value){
        if(data.containsKey(path)){
            String key = path.contains(".") ? path.substring(path.lastIndexOf('.')+1)
                    : path;
            if(groupBegins.containsKey(path)){
                for(int i = groupBegins.get(path); i < groupEnds.get(path)-1; ++i){
                    lines.remove(groupBegins.get(path).intValue());
                }
                if(value.contains("\n")){
                    int i = 0;
                    for(String v : value.split("\n")){
                        StringBuilder sb = new StringBuilder();
                        sb.repeat(' ',levels.get(path)+1).append(v);
                        lines.add(groupBegins.get(path) + i++, sb.toString());
                    }
                }else{
                   StringBuilder sb = new StringBuilder();

                    sb.repeat(' ', levels.get(path)).append(key).append(": ")
                            .append(value);
                    lines.set(groupBegins.get(path)-1, sb.toString());
                }
            }else{
                if(value.contains("\n")){
                    StringBuilder sb = new StringBuilder();
                    sb.repeat(' ',levels.get(path));
                    sb.append('[').append(key).append(']');
                    lines.set(pathsIndices.get(path)-1, sb.toString());
                    int i = 0;
                    for(String v : value.split("\n")) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.repeat(' ', levels.get(path) + 1).
                                append(v);
                        lines.add(pathsIndices.get(path) + i++, sb.toString());
                    }
                }else{
                    StringBuilder sb = new StringBuilder();
                    sb.repeat(' ',levels.get(path));
                    sb.append(key).append(": ").append(value);
                    lines.set(pathsIndices.get(path)-1, sb.toString());
                }
            }
        }else{
            if(path.contains(".")){

                String existedGroups = "", componentGroups[] = path.split("\\.");
                List<String> newGroups = new ArrayList<>();
                String key = "";
                for(int i = 0 ; i < componentGroups.length; ++i){
                    String groupName = componentGroups[i];
                    String temporary = existedGroups.isEmpty() ? groupName : (new StringBuilder(existedGroups).append(".").append(groupName)).toString();
                    if(groupBegins.containsKey(temporary)){
                        existedGroups = temporary;
                    }else if(i == componentGroups.length-1 && !value.contains("\n")){
                        key = groupName;
                    }
                    else{
                        newGroups.add(groupName);
                    }
                }
                int currentIndex = existedGroups.isEmpty() ? lines.size() : groupEnds.get(existedGroups)-1;
                int currentLevel = existedGroups.isEmpty() ? 0 : (levels.get(existedGroups) + 1);
                for(ListIterator<String> it = newGroups.listIterator(); it.hasNext();){
                    String groupName = it.next();
                    lines.add(currentIndex++, new StringBuilder().repeat(' ',currentLevel++).append("[").append(groupName).append("]").toString());
                }
                if(!value.contains("\n"))
                lines.add(currentIndex, new StringBuilder().repeat(' ', currentLevel).append(key).append(": ").append(value).toString());
                else{
                    for(String v : value.split("\n")){
                        lines.add(currentIndex++, new StringBuilder().repeat(' ',currentLevel).append(v).toString());
                    }
                }
            }else{
                if(value.contains("\n")){
                    lines.add(new StringBuilder().append("[").append(path).append("]").toString());
                    for(String v : value.split("\n")){
                        lines.add(new StringBuilder(" ").append(v).toString());
                    }
                }else {
                    lines.add(new StringBuilder(path).append(": ").append(value).toString());
                }
            }
        }
        update();
    }
    boolean delete(String path){
        if(groupBegins.containsKey(path)){
            int index = groupBegins.get(path)-1;
            for(int i = 0; i < groupEnds.get(path) - groupBegins.get(path); ++i){
                lines.remove(index);
            }
            update();
            return true;
        }
        else if(data.containsKey(path)){
            int index = pathsIndices.get(path) -1;
            lines.remove(index);
            update();
            return true;

        }else{
            return false;
        }
    }
    void saveToFile(){
        if(f == null)
        { f = new File(new StringBuilder("./").append(Date.from(Instant.now()).toString()).append(".txt").toString());}
        try {
            PrintWriter pw = new PrintWriter(f);
            for (ListIterator<String> it = lines.listIterator(); it.hasNext(); ){
                String ln = it.next();
                pw.println(ln);
                pw.flush();
            }
            pw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    void update(){
        data.clear();
        groupBegins.clear();
        groupEnds.clear();
        pathsIndices.clear();
        levels.clear();
        StringBuilder str = new StringBuilder();
        for(ListIterator<String> it = lines.listIterator(); it.hasNext(); ){
            String line = it.next();
            str.append(line).append("\n");
        }
        str.deleteCharAt(str.length()-1);
        lines.clear();
        read(str.toString());
    }
    public String get(String path){
        return data.get(path);
    }
}
