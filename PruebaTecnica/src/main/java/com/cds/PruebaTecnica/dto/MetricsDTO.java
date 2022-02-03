package com.cds.PruebaTecnica.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class MetricsDTO
{
    @JsonProperty("n_rows_missing_fields")
    private Integer nRowsMissingFields;

    @JsonProperty("n_messages_blank_content")
    private Integer nMessagesBlankContent;

    @JsonProperty("n_rows_fields_errors")
    private Integer nRowsFieldsErrors;

    @JsonProperty("n_calls_origin_destiny")
    private Map<String, Integer> nCallsOriginDestinyMap;

    @JsonProperty("ok_ko_relationship")
    private String okKoRelationShip;

    @JsonProperty("average_call_duration")
    private Map<String, Float> averageCallDurationMap;

    @JsonProperty("word_occurrence_ranking")
    private Map<String, Integer> wordOccurrenceMap;

    @JsonIgnore
    private Integer okCount;

    @JsonIgnore
    private Integer koCount;

    @JsonIgnore
    private Integer callCount;

    public MetricsDTO()
    {
        this.nRowsMissingFields = 0;
        this.nMessagesBlankContent = 0;
        this.nRowsFieldsErrors = 0;
        this.nCallsOriginDestinyMap = new HashMap<>();
        this.averageCallDurationMap = new HashMap<>();
        this.wordOccurrenceMap = new HashMap<>();
        this.okCount = 0;
        this.koCount = 0;
        this.callCount = 0;
    }
}
