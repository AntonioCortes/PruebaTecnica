package com.cds.PruebaTecnica.controller;

import com.cds.PruebaTecnica.constant.Constant;
import com.cds.PruebaTecnica.dto.JSONProcessDurationDTO;
import com.cds.PruebaTecnica.dto.KPIDTO;
import com.cds.PruebaTecnica.dto.MetricsDTO;
import com.cds.PruebaTecnica.entity.Call;
import com.cds.PruebaTecnica.entity.Message;
import com.cds.PruebaTecnica.entity.ProcessedJSON;
import com.cds.PruebaTecnica.entity.Row;
import com.cds.PruebaTecnica.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/log")
@CrossOrigin
public class LogController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    @Autowired
    LogService logService;

    @GetMapping
    public ResponseEntity<HttpStatus> selectLog(@RequestParam("date") final String pDate)
    {
        try
        {
            logService.selectLog(pDate);
        }
        catch(FileNotFoundException e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(IOException e)
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/metrics")
    public ResponseEntity<MetricsDTO> getMetrics() throws IOException
    {
        MetricsDTO metricsDTO = logService.getMetrics();

        return new ResponseEntity<>(metricsDTO, HttpStatus.OK);
    }

    @GetMapping("/kpis")
    public ResponseEntity<KPIDTO> getKPIs()
    {
        List<ProcessedJSON> processedJSONList = logService.getProcessedJSONList();
        List<Row> rowList = logService.getRowList();
        List<Call> callList = logService.getCallList();
        List<Message> messageList = logService.getMessageList();
        List<String> originCountriesList = new ArrayList<>();
        List<String> destinationCountriesList = new ArrayList<>();
        List<JSONProcessDurationDTO> jsonProcessDurationDTOList = new ArrayList<>();

        KPIDTO kpiDTO = new KPIDTO()
                .setTotalNProcessedJSON(processedJSONList.size())
                .setTotalNRows(rowList.size())
                .setTotalNCalls(callList.size())
                .setTotalNMessages(messageList.size());

        rowList.forEach(row ->
        {
            if(row.getOrigin() != null && row.getOrigin().length() > 2)
            {
                String originCountry = row.getOrigin().substring(0, 2);
                if(!originCountriesList.contains(originCountry))
                {
                    originCountriesList.add(originCountry);
                }
            }

            if(row.getDestination() != null && row.getDestination().length() > 2)
            {
                String destinationCountry = row.getDestination().substring(0, 2);
                if(!destinationCountriesList.contains(destinationCountry))
                {
                    destinationCountriesList.add(destinationCountry);
                }
            }
        });

        kpiDTO.setTotalNOriginCountries(originCountriesList.size())
                .setTotalNDestinationCountries(destinationCountriesList.size());

        processedJSONList.forEach(json ->
        {
            jsonProcessDurationDTOList.add(new JSONProcessDurationDTO()
                    .setLogFile(MessageFormat.format(Constant.LOG_NAME, json.getDate()))
                    .setDuration(json.getProcessDuration()));
        });

        kpiDTO.setJsonDurationList(jsonProcessDurationDTOList);

        return new ResponseEntity<>(kpiDTO, HttpStatus.OK);
    }
}
