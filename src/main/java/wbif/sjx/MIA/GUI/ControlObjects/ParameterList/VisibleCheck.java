package wbif.sjx.MIA.GUI.ControlObjects.ParameterList;

import wbif.sjx.MIA.GUI.ControlObjects.ModuleEnabledCheck;
import wbif.sjx.MIA.GUI.GUI;
import wbif.sjx.MIA.Object.Parameters.Abstract.Parameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by sc13967 on 06/06/2017.
 */
public class VisibleCheck extends JButton implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 3462766918524878171L;
    private Parameter parameter;

    private static final ImageIcon closedIcon = new ImageIcon(ModuleEnabledCheck.class.getResource("/Icons/eyeclosed_black_12px.png"), "");
    private static final ImageIcon openIcon = new ImageIcon(ModuleEnabledCheck.class.getResource("/Icons/eyeopen_black_12px.png"), "");

    public VisibleCheck(Parameter parameter) {
        this.parameter = parameter;

        addActionListener(this);
        setFocusPainted(false);
        setSelected(parameter.isVisible());
        setMargin(new Insets(0,0,0,0));
        setName("Show parameter");
        setToolTipText("Show parameter on basic GUI");
        updateIcon();

    }

    public void updateIcon() {
        if (parameter.isVisible()) setIcon(openIcon);
        else setIcon(closedIcon);
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GUI.addUndo();

        parameter.setVisible(!parameter.isVisible());
        updateIcon();

    }
}
