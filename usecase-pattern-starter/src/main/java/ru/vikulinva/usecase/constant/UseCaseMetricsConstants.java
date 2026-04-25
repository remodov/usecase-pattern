package ru.vikulinva.usecase.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UseCaseMetricsConstants {
    public static final String LABEL_USECASE_NAME = "usecase_name";
    public static final String LABEL_APPLICATION = "application";
    public static final String METRIC_SUCCESS = "usecase_success_total";
    public static final String METRIC_FAILURE = "usecase_failure_total";
    public static final String METRIC_DURATION = "usecase_duration_seconds";
    public static final String APP_NAME_PROPERTY = "spring.application.name";
}

