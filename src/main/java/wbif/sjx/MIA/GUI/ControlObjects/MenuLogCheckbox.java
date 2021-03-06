package wbif.sjx.MIA.GUI.ControlObjects;

import org.apache.commons.lang.WordUtils;
import wbif.sjx.MIA.MIA;
import wbif.sjx.MIA.Process.Logging.LogRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuLogCheckbox extends JCheckBoxMenuItem implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = -114208262023653742L;
    private final LogRenderer.Level level;

    public MenuLogCheckbox(LogRenderer.Level level, boolean state) {
        this.level = level;
        String title = WordUtils.capitalizeFully(level.toString());
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        setText(title);
        addActionListener(this);
        setSelected(state);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MIA.getMainRenderer().setWriteEnabled(level,isSelected());
    }
}
