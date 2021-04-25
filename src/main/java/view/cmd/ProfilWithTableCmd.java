package view.cmd;

import model.PojoBuilder;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import view.interfaces.IRootTable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@PojoBuilder
public class ProfilWithTableCmd {

    @NotBlank
    private String titlePanel;

    @NotBlank
    private String titleTablePanel;

    private String defaultProfile;

    @NotNull
    private ILexicometricConfiguration lexicometricConfiguration;

    @NotEmpty
    private Set<IRootTable> iRootTableSet;

    public String getTitlePanel() {
        return titlePanel;
    }

    public void setTitlePanel(String titlePanel) {
        this.titlePanel = titlePanel;
    }

    public String getTitleTablePanel() {
        return titleTablePanel;
    }

    public void setTitleTablePanel(String titleTablePanel) {
        this.titleTablePanel = titleTablePanel;
    }

    public String getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(String defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public ILexicometricConfiguration getLexicometricConfiguration() {
        return lexicometricConfiguration;
    }

    public void setLexicometricConfiguration(ILexicometricConfiguration lexicometricConfiguration) {
        this.lexicometricConfiguration = lexicometricConfiguration;
    }

    public Set<IRootTable> getiRootTableSet() {
        return iRootTableSet;
    }

    public void setiRootTableSet(Set<IRootTable> iRootTableSet) {
        this.iRootTableSet = iRootTableSet;
    }
}
