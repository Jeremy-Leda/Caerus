package view.abstracts;

import controler.ConfigurationControler;
import controler.IConfigurationControler;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Option;
import io.vavr.control.Try;
import model.exceptions.ErrorCode;
import model.exceptions.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionOnClose;
import view.utils.ConfigurationUtils;
import view.windows.ProgressBarView;
import view.windows.UserInformation;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Classe d'abstraction pour gérer les actions coté serveur
 *
 */
public abstract class ExecuteServerAbstract extends JFrame {

    private static Logger logger = LoggerFactory.getLogger(ExecuteServerAbstract.class);
    IConfigurationControler configurationControler;

    /**
     * Constructeur
     * @param configurationControler controller
     */
    public ExecuteServerAbstract(IConfigurationControler configurationControler) {
        this.configurationControler = configurationControler;
    }

    public <R extends Object> Option<R> executeOnServer(CheckedFunction0<R> function) {
        return Try.of(function::apply)
                .onFailure(ServerException.class, this::logAndCreateErrorInterface)
                .toOption();
    }


    public void executeOnServer(CheckedRunnable runnable) {
        executeOnServer(runnable, Boolean.FALSE);
    }

    public void executeOnServerWithProgressView(CheckedRunnable runnable, Boolean closeCurrentFrameOnSucceed, String ConstantLabelProgress, Boolean showSucceedPanel) {
        Try.run(() -> {
            new ProgressBarView(r -> {
                if (closeCurrentFrameOnSucceed) {
                    executeOnServerWithCloseCurrentFrame(runnable, showSucceedPanel);
                } else {
                    executeOnServer(runnable, showSucceedPanel);
                }
            }, getProgressConsumer(100), 100, ConfigurationUtils.getInstance().getDisplayMessage(ConstantLabelProgress));
            getControler().resetProgress();
        });
    }

    public void executeOnServerWithCloseCurrentFrame(CheckedRunnable runnable) {
        executeOnServerWithCloseCurrentFrame(runnable, Boolean.FALSE);
    }

    public void executeOnServerWithCloseCurrentFrame(CheckedRunnable runnable, Boolean showSucceedPanel) {
        Try.run(() -> runnable.run())
                .onFailure(ServerException.class, this::logAndCreateErrorInterface)
                .onSuccess(x -> { if (showSucceedPanel) { createSucceedInterface(); } })
                .onSuccess(x -> closeFrame());
    }


    public void executeOnServer(CheckedRunnable runnable, Boolean showSucceedPanel) {
        Try.run(() -> runnable.run())
                .onFailure(ServerException.class, this::logAndCreateErrorInterface)
                .onSuccess(x -> { if (showSucceedPanel) { createSucceedInterface(); } });
    }

    private void logAndCreateErrorInterface(ServerException serverException) {
        logger.error(serverException.toString(), serverException);
        if (serverException.getInformationExceptionSet().stream().anyMatch(informationException -> informationException.getErrorCode().equals(ErrorCode.TECHNICAL_ERROR))) {
            new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_FAILURE_TECHNICAL_PANEL_TITLE),
                    getControler(),
                    PictureTypeEnum.WARNING,
                    ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_FAILURE_TECHNICAL_LABEL));
        } else {
            String functionalErrors = serverException.getInformationExceptionSet().stream()
                    .map(informationException -> "<li>" + informationException.getErrorCode().getErrorLabel() + "</li>")
                    .collect(Collectors.joining("\n"));
            String errorLabel = String.format(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_FUNCTIONAL_ERROR_LIST_LABEL), functionalErrors);
            new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_FUNCTIONAL_ERROR_PANEL_TITLE),
                    getControler(),
                    PictureTypeEnum.WARNING,
                    errorLabel);
        }
    }


    private void createSucceedInterface() {
        new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_SUCCEED_PANEL_TITLE),
                getControler(),
                PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_SUCCEED_LABEL));
    }

    /**
     * Permet de se procurer le controler
     * @return le controler
     */
    protected IConfigurationControler getControler() {
        return this.configurationControler;
    }

    /**
     * Permet de se procurer le progress consumer
     *
     * @param progressMaxValue le maximum de la valeur
     * @return le progressConsumer
     */
    public Consumer<Consumer<Integer>> getProgressConsumer(Integer progressMaxValue) {
        return valueProgressSetter -> {
            while (this.configurationControler.getProgress() < progressMaxValue) {
                valueProgressSetter.accept(this.configurationControler.getProgress());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Permet de fermer la fenêtre
     */
    public abstract void closeFrame();

}