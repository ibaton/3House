package treehou.se.habit.core.db.controller;

import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Cells")
public class CellDB extends Model {

    public static final int TYPE_EMPTY         = 0;
    public static final int TYPE_BUTTON        = 1;
    public static final int TYPE_VOICE         = 2;
    public static final int TYPE_COLOR         = 3;
    public static final int TYPE_SLIDER        = 4;
    public static final int TYPE_INC_DEC       = 5;

    @Column(name = "CellRow", onDelete = Column.ForeignKeyAction.CASCADE)
    public CellRowDB cellRow;

    @Column(name = "type")
    public int type = TYPE_EMPTY;

    @Column(name = "color")
    public int color = Color.parseColor("#33000000");

    @Column(name = "label")
    public String label = "";

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CellRowDB getCellRow() {
        return cellRow;
    }

    public void setCellRow(CellRowDB cellRow) {
        this.cellRow = cellRow;
    }

    public SliderCellDB sliderCell(){
        List<SliderCellDB> numberCells = getMany(SliderCellDB.class, "Cell");
        return (numberCells.size()>0)?numberCells.get(0):null;
    }

    public ButtonCellDB buttonCell(){
        List<ButtonCellDB> buttonCells = getMany(ButtonCellDB.class, "Cell");
        return (buttonCells.size()>0)?buttonCells.get(0):null;
    }

    public ColorCellDB colorCell(){
        List<ColorCellDB> colorCells = getMany(ColorCellDB.class, "Cell");
        return (colorCells.size()>0)?colorCells.get(0):null;
    }


    public VoiceCellDB voiceCell(){
        List<VoiceCellDB> voiceCells = getMany(VoiceCellDB.class, "Cell");
        return (voiceCells.size()>0)?voiceCells.get(0):null;
    }

    public SwitchCellDB switchCell(){
        List<SwitchCellDB> switchCells = getMany(SwitchCellDB.class, "Cell");
        return (switchCells.size()>0)?switchCells.get(0):null;
    }

    public ChartCellDB chartCell(){
        List<ChartCellDB> chartCells = getMany(ChartCellDB.class, "Cell");
        return (chartCells.size()>0)?chartCells.get(0):null;
    }

    public IncDecCellDB incDecCell(){
        List<IncDecCellDB> incDecCells = getMany(IncDecCellDB.class, "Cell");
        return (incDecCells.size()>0)?incDecCells.get(0):null;
    }

    public StringCellDB stringCell(){
        List<StringCellDB> stringCells = getMany(StringCellDB.class, "Cell");
        return (stringCells.size()>0)?stringCells.get(0):null;
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

    @Override
    public String toString() {
        return "Cell{" +
                "cellRow=" + cellRow +
                ", type=" + type +
                '}';
    }
}
