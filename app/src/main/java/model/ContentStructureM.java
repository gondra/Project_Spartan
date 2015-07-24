package model;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentStructureM {
    String module;
    String label;
    ArrayList<Object> field;
    ArrayList<Object> relations;
    ArrayList<Object> permissions;
    HashMap data ;

    public ContentStructureM() {
        module = "";
        label = "";
        field = new ArrayList();
        relations = new ArrayList();
        permissions = new ArrayList();
        data = new HashMap();
    }

    public ArrayList<Object> getField() {
        return field;
    }

    public void setField(ArrayList<Object> field) {
        this.field = field;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public ArrayList<Object> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<Object> permissions) {
        this.permissions = permissions;
    }

    public ArrayList<Object> getRelations() {
        return relations;
    }

    public void setRelations(ArrayList<Object> relations) {
        this.relations = relations;
    }

    public HashMap getData() {
        return data;
    }

    public void setData(HashMap data) {
        this.data = data;
    }


}
