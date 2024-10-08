package com.siewe_rostand.tvcam.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rostand
 * @project tv-cam
 */

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PaginatedResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String message;
    protected List<?> data;
    protected Boolean lastPage;
    protected Boolean firstPage;
    protected Boolean empty;
    protected Boolean sorted;
    protected Integer totalPages;
    protected Integer totalElements;
    protected Integer numberOfElements;
    protected Integer page;
    protected Boolean paged;
}
