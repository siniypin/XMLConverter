package com.piskunov.xmlconverter.mapping;

/**
 * Created by Vladimir Piskunov on 2/28/16.
 */
public class MappingException extends Exception {

    private static int nextID = 0;

    private static int getNextID(){
        return nextID++;
    }

    private static int getID(){
        return nextID;
    }

    private int errorID;

    public MappingException(String s) {
        super(s);
        errorID = getNextID();
    }

    public MappingException(MappingRule rule, String msg) {
        this("Mapping error: " + msg + " ID" + getID() + " TargetField: " + rule.getTarget() + " Source: " + rule.getSource() + " Adapter: " + rule.getAdapter().getClass().getSimpleName());
    }

    public int getErrorID() {
        return errorID;
    }
}
