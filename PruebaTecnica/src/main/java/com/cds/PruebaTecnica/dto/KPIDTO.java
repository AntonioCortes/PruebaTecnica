package com.cds.PruebaTecnica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

@Data
@Accessors(chain = true)
public class KPIDTO
{
    @JsonProperty("total_n_processed_json")
    private Integer totalNProcessedJSON;

    @JsonProperty("total_n_rows")
    private Integer totalNRows;

    @JsonProperty("total_n_calls")
    private Integer totalNCalls;

    @JsonProperty("total_n_messages")
    private Integer totalNMessages;

    @JsonProperty("total_n_origin_countries")
    private Integer totalNOriginCountries;

    @JsonProperty("total_n_destination_countries")
    private Integer totalNDestinationCountries;

    @JsonProperty("duration_each_json_process")
    private List<JSONProcessDurationDTO> jsonDurationList;
}
