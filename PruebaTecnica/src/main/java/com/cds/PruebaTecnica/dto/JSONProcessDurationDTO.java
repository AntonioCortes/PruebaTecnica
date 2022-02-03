package com.cds.PruebaTecnica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JSONProcessDurationDTO
{
    @JsonProperty("log_file")
    private String logFile;

    @JsonProperty("process_duration_nanoseconds")
    private Long duration;
}
