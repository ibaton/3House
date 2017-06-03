package treehou.se.habit.core.db.model.controller;

import android.graphics.Color;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CellDB extends RealmObject {

    public static final int TYPE_EMPTY         = 0;
    public static final int TYPE_BUTTON        = 1;
    public static final int TYPE_VOICE         = 2;
    public static final int TYPE_COLOR         = 3;
    public static final int TYPE_SLIDER        = 4;
    public static final int TYPE_INC_DEC       = 5;

    @PrimaryKey
    private long id = 0;
    private CellRowDB cellRow;
    private int color = Color.parseColor("#33000000");
    private String label = "";

    private ButtonCellDB cellButton = null;
    private ColorCellDB cellColor = null;
    private IncDecCellDB cellIncDec = null;
    private SliderCellDB cellSlider = null;
    private VoiceCellDB cellVoice = null;

    public int getType() {
        if(cellButton != null){
            return TYPE_BUTTON;
        } else if(cellVoice != null){
            return TYPE_VOICE;
        } else if(cellColor != null){
            return TYPE_COLOR;
        } else if(cellSlider != null){
            return TYPE_SLIDER;
        } else if(cellIncDec != null){
            return TYPE_INC_DEC;
        }

        return TYPE_EMPTY;
    }

    public void setCellButton(ButtonCellDB cellButton) {
        clearCellData();
        this.cellButton = cellButton;

    }

    public void setCellColor(ColorCellDB cellColor) {
        clearCellData();
        this.cellColor = cellColor;
    }

    public void setCellIncDec(IncDecCellDB cellIncDec) {
        clearCellData();
        this.cellIncDec = cellIncDec;
    }

    public void setCellSlider(SliderCellDB cellSlider) {
        clearCellData();
        this.cellSlider = cellSlider;
    }

    public void setCellVoice(VoiceCellDB cellVoice) {
        clearCellData();
        this.cellVoice = cellVoice;
    }

    public ButtonCellDB getCellButton() {
        return cellButton;
    }

    public ColorCellDB getCellColor() {
        return cellColor;
    }

    public IncDecCellDB getCellIncDec() {
        return cellIncDec;
    }

    public SliderCellDB getCellSlider() {
        return cellSlider;
    }

    public VoiceCellDB getCellVoice() {
        return cellVoice;
    }

    private void clearCellData(){
        cellButton = null;
        cellColor = null;
        cellIncDec = null;
        cellSlider = null;
        cellVoice = null;
    }

    public CellRowDB getCellRow() {
        return cellRow;
    }

    public void setCellRow(CellRowDB cellRow) {
        this.cellRow = cellRow;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static CellDB load(Realm realm, long id){
        return realm.where(CellDB.class).equalTo("id", id).findFirst();
    }

    public static CellDB save(Realm realm, CellDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        CellDB cell = realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();

        return cell;
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(CellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
