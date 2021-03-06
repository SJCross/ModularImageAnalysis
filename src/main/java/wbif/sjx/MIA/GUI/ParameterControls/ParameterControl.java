package wbif.sjx.MIA.GUI.ParameterControls;

import javax.swing.JComponent;

import wbif.sjx.MIA.Object.Parameters.Abstract.Parameter;

public abstract class ParameterControl {
    protected Parameter parameter;
    public abstract JComponent getComponent();

    public abstract void updateControl();
    
    public ParameterControl(Parameter parameter) {
        this.parameter = parameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
