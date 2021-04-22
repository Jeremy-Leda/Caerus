package view.services;

import controler.IConfigurationControler;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Option;
import io.vavr.control.Try;
import model.exceptions.ErrorCode;
import model.exceptions.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ExecuteServerJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.utils.ConfigurationUtils;
import view.windows.UserInformation;

import java.util.stream.Collectors;

import static view.utils.Constants.*;
import static view.utils.Constants.WINDOW_FUNCTIONAL_ERROR_PANEL_TITLE;

/**
 *
 * Classe permettant de fournir des méthodes d'exécution pour des services afin de simplifier la prise en charge des erreurs d'éxécutions
 *
 */
public class ExecutionService {

    private static Logger logger = LoggerFactory.getLogger(ExecutionService.class);

    public ExecutionService() {
    }

    public void executeOnServer(CheckedRunnable runnable) {
        executeOnServer(runnable, Boolean.FALSE);
    }

    public void executeOnServer(CheckedRunnable runnable, Boolean showSucceedPanel) {
        Try.run(() -> runnable.run())
                .onFailure(ServerException.class, this::logAndCreateErrorInterface)
                .onSuccess(x -> { if (showSucceedPanel) { createSucceedInterface(); } });
    }

    public <R extends Object> Option<R> executeOnServer(CheckedFunction0<R> function) {
        return Try.of(function::apply)
                .onFailure(ServerException.class, this::logAndCreateErrorInterface)
                .toOption();
    }

    private void logAndCreateErrorInterface(ServerException serverException) {
        logger.error(serverException.toString(), serverException);
        if (serverException.getInformationExceptionSet().stream().anyMatch(informationException -> informationException.getErrorCode().equals(ErrorCode.TECHNICAL_ERROR))) {
            new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_FAILURE_TECHNICAL_PANEL_TITLE),
                    null,
                    PictureTypeEnum.WARNING,
                    ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_FAILURE_TECHNICAL_LABEL));
        } else {
            String functionalErrors = serverException.getInformationExceptionSet().stream()
                    .map(informationException -> "<li>" + informationException.getErrorCode().getErrorLabel() + "</li>")
                    .collect(Collectors.joining("\n"));
            String errorLabel = String.format(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_FUNCTIONAL_ERROR_LIST_LABEL), functionalErrors);
            new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_FUNCTIONAL_ERROR_PANEL_TITLE),
                    null,
                    PictureTypeEnum.WARNING,
                    errorLabel);
        }
    }

    private void createSucceedInterface() {
        new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_SUCCEED_PANEL_TITLE),
                null,
                PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_OPERATION_SUCCEED_LABEL));
    }
}
