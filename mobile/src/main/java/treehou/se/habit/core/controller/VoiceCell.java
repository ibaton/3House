package treehou.se.habit.core.controller;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.core.db.model.controller.VoiceCellDB;

public class VoiceCell {

    private VoiceCellDB voiceCellDB;

    public VoiceCell() {
    }

    public VoiceCell(VoiceCellDB voiceCellDB) {
        this.voiceCellDB = voiceCellDB;
    }

    public VoiceCellDB getDB() {
        return voiceCellDB;
    }

    public void getDB(VoiceCellDB voiceCellDB) {
        this.voiceCellDB = voiceCellDB;
    }

    public long getId() {
        return getDB().getId();
    }

    public void setId(long id) {
        getDB().setId(id);
    }

    public OHItem getItem() {
        //return getDB().getItem();
        return null;
    }

    public void setItem(OHItem item) {
        //getDB().setItem(item);
    }

    public String getIcon() {
        return getDB().getIcon();
    }

    public void setIcon(String icon) {
        getDB().setIcon(icon);
    }

    public Cell getCell() {
        return new Cell(getDB().getCell());
    }

    public void setCell(Cell cell) {
        getDB().setCell(cell.getDB());
    }

    public void save(){
        //VoiceCellDB.save(getDB());
    }

    public static VoiceCell getCell(Cell cell){
        return null;//new VoiceCell(VoiceCellDB.getCell(cell.getDB()));
    }
}
