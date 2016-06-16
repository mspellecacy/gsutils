package gsutils.core;

/**
 * Created by mspellecacy on 6/12/2016.
 */
public class OutputOption {
    private String displayName;
    private String currentValue;
    private String symbolValue;

    public OutputOption(String displayName, String currentValue, String symbolValue) {
        this.displayName = displayName;
        this.currentValue = currentValue;
        this.symbolValue = symbolValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getSymbolValue() {
        return symbolValue;
    }

    public void setSymbolValue(String symbolValue) {
        this.symbolValue = symbolValue;
    }


}