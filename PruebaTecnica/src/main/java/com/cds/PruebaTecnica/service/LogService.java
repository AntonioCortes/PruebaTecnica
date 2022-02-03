package com.cds.PruebaTecnica.service;

import com.cds.PruebaTecnica.dto.MetricsDTO;
import com.cds.PruebaTecnica.entity.Call;
import com.cds.PruebaTecnica.entity.Message;
import com.cds.PruebaTecnica.entity.ProcessedJSON;
import com.cds.PruebaTecnica.entity.Row;

import java.io.IOException;
import java.util.List;

public interface LogService
{
    void selectLog(String pDate) throws IOException;

    MetricsDTO getMetrics() throws IOException;

    List<ProcessedJSON> getProcessedJSONList();

    List<Row> getRowList();

    List<Call> getCallList();

    List<Message> getMessageList();
}
