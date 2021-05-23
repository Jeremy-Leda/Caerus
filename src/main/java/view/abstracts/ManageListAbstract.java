package view.abstracts;

import controler.IConfigurationControler;
import io.vavr.Function2;
import org.apache.commons.lang3.StringUtils;
import view.beans.LexicometricEditEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IRootTable;
import view.interfaces.ITableFilterPanel;
import view.interfaces.ITableWithFilterAndEditPanel;
import view.panel.ActionPanel;
import view.panel.TableFilterTextPanel;
import view.panel.TableWithFilterAndEditPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * Classe d'abstraction pour la gestion des listes
 *
 */
public abstract class ManageListAbstract extends ModalJFrameAbstract {

    public ManageListAbstract(String title, IConfigurationControler configurationControler) {
        super(title, configurationControler);
    }

    /**
     * Permet de se procurer une nouvelle instance du panel d'action par défaut
     * @param lexicometricEditEnum type de liste
     * @return la nouvelle instance du panel d'action par défaut
     */
    public IActionPanel getNewInstanceOfDefaultAction(LexicometricEditEnum lexicometricEditEnum) {
        IActionPanel actionPanel = new ActionPanel(2);
        actionPanel.setIconButton(0, PictureTypeEnum.SAVE);
        actionPanel.addAction(0, x -> {
            executeOnServer(() -> getControler().saveLexicometricAllProfilInDisk(lexicometricEditEnum), true);
            closeFrame();
        });
        actionPanel.addAction(1, x -> closeFrame());
        actionPanel.setStaticLabel(getMessage(Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL),
                Map.of(0, getMessage(Constants.WINDOW_EDIT_PROFILE_SAVE_ALL_PROFILES_AND_QUIT),
                        1, getMessage(Constants.WINDOW_INFORMATION_ACTION_BUTTON_LABEL)));
        return actionPanel;
    }

    /**
     * Pemret de se procurer le filtre par défaut pour la gestion des listes
     * @return le filtre par défaut
     */
    public Function2<IRootTable, Consumer<?>, ITableWithFilterAndEditPanel> getTableWithTextFilterAndEditPanelFunction() {
        return (x, v) -> new TableWithFilterAndEditPanel<String>(StringUtils.EMPTY, x.getHeaderLabel(), v,
                Comparator.naturalOrder(),
                Comparator.comparing(StringUtils::stripAccents),
                s -> StringUtils.isNotBlank(s.getStringValue()),
                (s, f) -> s.toLowerCase(Locale.ROOT).contains(f.getStringValue()), getTableTextFilterPanel(x.getFilterLabel()));
    }

    /**
     * Permet de se procurer le table filter pour filtrer par rapport à une chaine de caractère
     * @param label Label du filtre
     * @return le table filter pour filtrer par rapport à une chaine de caractère
     */
    private ITableFilterPanel getTableTextFilterPanel(String label) {
        TableFilterTextPanel tableFilterPanel = new TableFilterTextPanel();
        tableFilterPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, label));
        return tableFilterPanel;
    }
}
