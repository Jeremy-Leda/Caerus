package model.exceptions;

import view.utils.ConfigurationUtils;

import static view.utils.Constants.*;

public enum ErrorCode {
    TECHNICAL_ERROR(WINDOW_OPERATION_FAILURE_TECHNICAL_LABEL),
    FILE_NOT_EXIST(WINDOW_FUNCTIONAL_ERROR_FILE_NOT_EXIST),
    NONE_FIELD_SELECTED(WINDOW_FUNCTIONAL_ERROR_NONE_FIELD_SELECTED),
    ERROR_CONFIGURATION(WINDOW_FUNCTIONAL_ERROR_INVALID_CONFIGURATION),
    INVALID_FIELD_WITH_CONFIGURATION(WINDOW_FUNCTIONAL_ERROR_INVALID_FIELD_WITH_CONFIGURATION),
    INVALID_FILE_EXCEL(WINDOW_FUNCTIONAL_ERROR_INVALID_FILE_EXCEL),
    INVALID_SPECIFIC_CONFIGURATION(WINDOW_FUNCTIONAL_ERROR_INVALID_SPECIFIC_CONFIGURATION),
    INVALID_FILE_EXCEL_SPECIFIC_CONFIGURATION(WINDOW_FUNCTIONAL_ERROR_INVALID_FILE_EXCEL_SPECIFIC_CONFIGURATION),
    ERROR_ANALYZE_FOLDER(WINDOW_FUNCTIONAL_ERROR_INVALID_ANALYSIS_FOLDER);

    private final String errorCode;


    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorLabel() {
        return ConfigurationUtils.getInstance().getDisplayMessage(this.errorCode);
    }
}
