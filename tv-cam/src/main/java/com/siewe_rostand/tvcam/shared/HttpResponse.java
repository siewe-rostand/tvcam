package com.siewe_rostand.tvcam.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.DATE_TIME_FORMAT;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HttpResponse<T> {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    protected LocalDateTime timestamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String reason;
    protected String message;
    protected String path;
    protected String developerMessage;
    //    protected Map<?, ?> data;
    protected T data;
    protected Set<String> validationErrors;
    protected List<?> content;
    protected String errorSource;
    protected Throwable errorCause;
}