package treehou.se.habit.core.controller;

import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by ibaton on 2014-11-03.
 */

@Table(name = "Cells")
public class Cell extends Model {

    public static final int TYPE_EMPTY         = 0;
    public static final int TYPE_BUTTON        = 1;
    public static final int TYPE_VOICE         = 2;
    public static final int TYPE_COLOR         = 3;
    public static final int TYPE_SLIDER        = 4;
    public static final int TYPE_INC_DEC       = 5;

    @Column(name = "CellRow", onDelete = Column.ForeignKeyAction.CASCADE)
    public CellRow cellRow;

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

    public CellRow getCellRow() {
        return cellRow;
    }

    public void setCellRow(CellRow cellRow) {
        this.cellRow = cellRow;
    }

    public SliderCell sliderCell(){
        List<SliderCell> numberCells = getMany(SliderCell.class, "Cell");
        return (numberCells.size()>0)?numberCells.get(0):null;
    }

    public ButtonCell buttonCell(){
        List<ButtonCell> buttonCells = getMany(ButtonCell.class, "Cell");
        return (buttonCells.size()>0)?buttonCells.get(0):null;
    }

    public ColorCell colorCell(){
        List<ColorCell> colorCells = getMany(ColorCell.class, "Cell");
        return (colorCells.size()>0)?colorCells.get(0):null;
    }


    public VoiceCell voiceCell(){
        List<VoiceCell> voiceCells = getMany(VoiceCell.class, "Cell");
        return (voiceCells.size()>0)?voiceCells.get(0):null;
    }

    public SwitchCell switchCell(){
        List<SwitchCell> switchCells = getMany(SwitchCell.class, "Cell");
        return (switchCells.size()>0)?switchCells.get(0):null;
    }

    public ChartCell chartCell(){
        List<ChartCell> chartCells = getMany(ChartCell.class, "Cell");
        return (chartCells.size()>0)?chartCells.get(0):null;
    }

    public IncDecCell incDecCell(){
        List<IncDecCell> incDecCells = getMany(IncDecCell.class, "Cell");
        return (incDecCells.size()>0)?incDecCells.get(0):null;
    }

    public StringCell stringCell(){
        List<StringCell> stringCells = getMany(StringCell.class, "Cell");
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
