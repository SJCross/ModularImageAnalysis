package wbif.sjx.MIA.Object.Parameters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import wbif.sjx.MIA.MIA;
import wbif.sjx.MIA.GUI.GUI;
import wbif.sjx.MIA.GUI.ParameterControls.GenericButton;
import wbif.sjx.MIA.GUI.ParameterControls.ParameterControl;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.Core.OutputControl;
import wbif.sjx.MIA.Object.Parameters.Abstract.Parameter;

public class GenericButtonP extends Parameter {
    public enum DefaultModes {
        REFRESH, REFRESH_FILE, TEST_MACRO
    }

    protected String buttonLabel;
    protected ActionListener actionListener = null;

    public GenericButtonP(String name, Module module, String buttonLabel, DefaultModes defaultMode) {
        super(name, module);
        this.buttonLabel = buttonLabel;

        switch (defaultMode) {
            case REFRESH:
                this.actionListener = getRefreshActionListener();
                break;
            case TEST_MACRO:
                this.actionListener = getTestMacroActionListener();
                break;
        }

        setExported(false);

    }

    public GenericButtonP(String name, Module module, String buttonLabel, ActionListener actionListener) {
        super(name, module);
        this.buttonLabel = buttonLabel;
        this.actionListener = actionListener;
        setExported(false);

    }

    public GenericButtonP(String name, Module module, String buttonLabel, DefaultModes defaultMode,
            String description) {
        super(name, module, description);
        this.buttonLabel = buttonLabel;

        switch (defaultMode) {
            case REFRESH:
                this.actionListener = getRefreshActionListener();
                break;
            case REFRESH_FILE:
                this.actionListener = getRefreshFileActionListener();
                break;
            case TEST_MACRO:
                this.actionListener = getTestMacroActionListener();
                break;
        }

        setExported(false);
    }

    public GenericButtonP(String name, Module module, String buttonLabel, ActionListener actionListener,
            String description) {
        super(name, module, description);
        this.buttonLabel = buttonLabel;
        this.actionListener = actionListener;
        setExported(false);
    }

    public ActionListener getActionListener() {
        return actionListener;

    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;

    }

    @Override
    protected ParameterControl initialiseControl() {
        return new GenericButton(this);
    }

    @Override
    public <T> T getValue() {
        return (T) buttonLabel;
    }

    @Override
    public <T> void setValue(T value) {
        this.buttonLabel = (String) value;
    }

    @Override
    public String getRawStringValue() {
        return buttonLabel;
    }

    @Override
    public void setValueFromString(String string) {
        this.buttonLabel = string;
    }

    @Override
    public boolean verify() {
        return true;
    }

    @Override
    public <T extends Parameter> T duplicate(Module newModule) {
        GenericButtonP newParameter = new GenericButtonP(name, newModule, buttonLabel, actionListener,
                getDescription());

        newParameter.setNickname(getNickname());
        newParameter.setVisible(isVisible());
        newParameter.setExported(isExported());

        return (T) newParameter;

    }

    public ActionListener getRefreshActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    GUI.addUndo();

                    int idx = GUI.getModules().indexOf(getModule());
                    if (idx <= GUI.getLastModuleEval() & !(getModule() instanceof OutputControl))
                        GUI.setLastModuleEval(idx - 1);

                    GUI.updateModuleStates(true);
                    GUI.updateModules();
                    GUI.updateParameters();
                }).start();
            }
        };
    }

    public ActionListener getRefreshFileActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    GUI.addUndo();

                    int idx = GUI.getModules().indexOf(getModule());
                    if (idx <= GUI.getLastModuleEval() & !(getModule() instanceof OutputControl))
                        GUI.setLastModuleEval(idx - 1);

                    GUI.updateModuleStates(true);
                    GUI.updateTestFile(true);
                    GUI.updateModules();
                    GUI.updateParameters();
                }).start();
            }
        };
    }

    public ActionListener getTestMacroActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    GUI.addUndo();

                    OutputControl outputControl = GUI.getAnalysis().getModules().getOutputControl();
                    outputControl.runMacro(GUI.getTestWorkspace());
                }).start();
            }
        };
    }
}
