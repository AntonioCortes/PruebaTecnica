package com.cds.PruebaTecnica.dto;

import com.cds.PruebaTecnica.constant.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class LogDTO
{
    @JsonProperty(Constant.MESSAGE_TYPE)
    private String messageType;

    @JsonProperty(Constant.TIMESTAMP)
    private String timestamp;

    @JsonProperty(Constant.ORIGIN)
    private String origin;

    @JsonProperty(Constant.DESTINATION)
    private String destination;

    @JsonProperty(Constant.DURATION)
    private Integer duration;

    @JsonProperty(Constant.STATUS_CODE)
    private String statusCode;

    @JsonProperty(Constant.STATUS_DESCRIPTION)
    private String statusDescription;

    @JsonProperty(Constant.MESSAGE_CONTENT)
    private String messageContent;

    @JsonProperty(Constant.MESSAGE_STATUS)
    private String messageStatus;
}