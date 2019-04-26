package wbif.sjx.MIA.Object.Parameters;

import wbif.sjx.MIA.GUI.ParameterControls.ParameterControl;
import wbif.sjx.MIA.GUI.ParameterControls.SeparatorParameter;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Object.Parameters.Abstract.Parameter;

import javax.swing.*;
import java.awt.*;

public class GUISeparatorP extends Parameter {
    public GUISeparatorP(String name, Module module) {
        super(name, module);
        setExported(false);

    }

    @Override
    protected ParameterControl initialiseControl() {
        return new SeparatorParameter(this);

    }

    @Override
    public <T> T getValue() {
        return null;
    }

    @Override
    public <T> void setValue(T value) {

    }

    @Override
    public String getValueAsString() {
        return null;
    }

    @Override
    public boolean verify() {
        return true;
    }

    @Override
    public <T extends Parameter> T duplicate() {
        return (T) new GUISeparatorP(name,module);
    }
}