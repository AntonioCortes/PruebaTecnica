package com.cds.PruebaTecnica.service.impl;

import com.cds.PruebaTecnica.constant.Constant;
import com.cds.PruebaTecnica.dto.LogDTO;
import com.cds.PruebaTecnica.dto.MetricsDTO;
import com.cds.PruebaTecnica.entity.Call;
import com.cds.PruebaTecnica.entity.Message;
import com.cds.PruebaTecnica.entity.ProcessedJSON;
import com.cds.PruebaTecnica.entity.Row;
import com.cds.PruebaTecnica.repository.CallRepository;
import com.cds.PruebaTecnica.repository.MessageRepository;
import com.cds.PruebaTecnica.repository.ProcessedJSONRepository;
import com.cds.PruebaTecnica.repository.RowRepository;
import com.cds.PruebaTecnica.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LogServiceImpl implements LogService
{
    @Autowired
    ProcessedJSONRepository processedJSONRepository;

    @Autowired
    RowRepository rowRepository;

    @Autowired
    CallRepository callRepository;

    @Autowired
    MessageRepository messageRepository;

    @Override
    public void selectLog(String pDate) throws FileNotFoundException, IOException
    {
        String jsonUrlString = MessageFormat.format(Constant.LOG_URL, pDate);
        URL jsonUrl =  new URL(jsonUrlString);
        InputStream inputStream;

        try
        {
            inputStream = jsonUrl.openStream();
        }
        catch(IOException e)
        {
            throw new FileNotFoundException();
        }

        inputStream.close();

        ProcessedJSON processedJSON = new ProcessedJSON()
                .setDate(pDate);

        processedJSONRepository.save(processedJSON);
    }

    @Override
    public MetricsDTO getMetrics() throws IOException
    {
        List<ProcessedJSON> processedJSONList = processedJSONRepository.findAll();
        MetricsDTO metricsDTO = new MetricsDTO();

        if(!processedJSONList.isEmpty())
        {
            ProcessedJSON processedJSON = processedJSONList.get(processedJSONList.size() - 1);
            String date = processedJSON.getDate();
            String jsonUrlString = MessageFormat.format(Constant.LOG_URL, date);
            URL jsonUrl =  new URL(jsonUrlString);
            InputStream inputStream;

            try
            {
                inputStream = jsonUrl.openStream();
            }
            catch(IOException e)
            {
                throw new FileNotFoundException();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            long startTime = System.nanoTime();
            String line = "";
            while((line =  bufferedReader.readLine()) != null)
            {
                if(processedJSON.getRowList() == null)
                {
                    processedJSON.setRowList(new ArrayList<>());
                }

                Row row = new Row()
                        .setFieldsErrors(false)
                        .setMissingFields(false);
                try
                {
                    checkFields(row, line, metricsDTO);
                    row.setProcessedJSON(processedJSON);
                    processedJSON.getRowList().add(row);
                }
                catch(IOException e)
                {
                    metricsDTO.setNRowsFieldsErrors(metricsDTO.getNRowsFieldsErrors() + 1);
                    row.setFieldsErrors(true);
                    row.setProcessedJSON(processedJSON);
                    processedJSON.getRowList().add(row);
                }
            }

            inputStream.close();
            bufferedReader.close();

            metricsDTO.getAverageCallDurationMap().forEach((countryCode, count) ->
            {
                metricsDTO.getAverageCallDurationMap().put(countryCode, count / metricsDTO.getNCallsOriginDestinyMap().get(countryCode))    ;
            });
            metricsDTO.setOkKoRelationShip(MessageFormat.format("{0}/{1}", metricsDTO.getOkCount(), metricsDTO.getKoCount()));

            long processDuration = System.nanoTime() - startTime;
            processedJSON.setProcessDuration(processDuration);
            processedJSONRepository.save(processedJSON);
        }

        return metricsDTO;
    }

    private void checkFields(final Row pRow, final String pLine, final MetricsDTO pMetricsDTO) throws IOException
    {
        LogDTO logDTO = new ObjectMapper().readValue(pLine, LogDTO.class);
        pRow.setMessageType(logDTO.getMessageType())
                .setOrigin(logDTO.getOrigin())
                .setDestination(logDTO.getDestination());

        checkCommonFields(logDTO, pRow, pLine, pMetricsDTO);

        if((logDTO.getMessageType() != null && logDTO.getMessageType().equals(Constant.CALL)) || pLine.contains(Constant.DURATION) || pLine.contains(Constant.STATUS_CODE) || pLine.contains(Constant.STATUS_DESCRIPTION))
        {
            checkCallFields(logDTO, pRow, pLine, pMetricsDTO);
        }
        else
        {
            checkMessageFields(logDTO, pRow, pLine, pMetricsDTO);
        }
    }

    private void checkCommonFields(LogDTO pLogDTO, Row pRow, String pLine, MetricsDTO pMetricsDTO)
    {
        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.MESSAGE_TYPE)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getMessageType().matches(MessageFormat.format("^({0}|{1})$", Constant.CALL, Constant.MSG)))
        {
            pRow.setFieldsErrors(true);
        }

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.TIMESTAMP)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getTimestamp().matches("^[1-9][0-9]*$"))
        {
            pRow.setFieldsErrors(true);
        }
        else
        {
            pRow.setTimestamp(new Timestamp(Integer.parseInt(pLogDTO.getTimestamp())));
        }

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.ORIGIN)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getOrigin().matches("^[0-9]{2,15}$"))
        {
            pRow.setFieldsErrors(true);
        }

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.DESTINATION)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getDestination().matches("^[0-9]{2,15}$"))
        {
            pRow.setFieldsErrors(true);
        }
    }

    private void checkCallFields(LogDTO pLogDTO, Row pRow, String pLine, MetricsDTO pMetricsDTO)
    {
        Call call = new Call()
                .setDuration(pLogDTO.getDuration())
                .setStatusCode(pLogDTO.getStatusCode())
                .setStatusDescription(pLogDTO.getStatusDescription());

        call.setRow(pRow);
        pRow.setCall(call);

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.DURATION)))
        {
            pRow.setMissingFields(true);
        }
        else if(pLogDTO.getDuration() == null)
        {
            pRow.setFieldsErrors(true);
        }

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.STATUS_CODE)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getStatusCode().matches(MessageFormat.format("^({0}|{1})$", Constant.OK, Constant.KO)))
        {
            pRow.setFieldsErrors(true);
        }
        else
        {
            if(pLogDTO.getStatusCode().equals(Constant.OK))
            {
                pMetricsDTO.setOkCount(pMetricsDTO.getOkCount() + 1);
            }
            else
            {
                pMetricsDTO.setKoCount(pMetricsDTO.getKoCount() + 1);
            }
        }

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.STATUS_DESCRIPTION)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getStatusDescription().matches("^[0-9a-zA-Z]+$"))
        {
            pRow.setFieldsErrors(true);
        }

        if(pLogDTO.getOrigin() != null && pLogDTO.getOrigin().matches("^[0-9]{2,15}$")
                && pLogDTO.getDestination() != null && pLogDTO.getDestination().matches("^[0-9]{2,15}$"))
        {
            String originDestinationCountryCode = MessageFormat.format("{0}-{1}", pLogDTO.getOrigin().substring(0, 2), pLogDTO.getDestination().substring(0, 2));

            if(!pMetricsDTO.getNCallsOriginDestinyMap().containsKey(originDestinationCountryCode))
            {
                pMetricsDTO.getNCallsOriginDestinyMap().put(originDestinationCountryCode, 0);
            }
            int previousCountValue = pMetricsDTO.getNCallsOriginDestinyMap().get(originDestinationCountryCode);
            pMetricsDTO.getNCallsOriginDestinyMap().put(originDestinationCountryCode, previousCountValue + 1);

            if(pLogDTO.getDuration() != null)
            {
                if(!pMetricsDTO.getAverageCallDurationMap().containsKey(originDestinationCountryCode))
                {
                    pMetricsDTO.getAverageCallDurationMap().put(originDestinationCountryCode, 0F);
                }

                pMetricsDTO.getAverageCallDurationMap().put(originDestinationCountryCode, pMetricsDTO.getAverageCallDurationMap().get(originDestinationCountryCode) + pLogDTO.getDuration());
            }
        }
    }

    private void checkMessageFields(LogDTO pLogDTO, Row pRow, String pLine, MetricsDTO pMetricsDTO)
    {
        Message message = new Message()
                .setMessageStatus(pLogDTO.getMessageStatus())
                .setMessageContent(pLogDTO.getMessageContent());

        message.setRow(pRow);
        pRow.setMessage(message);

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.MESSAGE_CONTENT)))
        {
            pRow.setMissingFields(true);
        }
        else if(pLogDTO.getMessageContent().isBlank())
        {
            pMetricsDTO.setNMessagesBlankContent(pMetricsDTO.getNMessagesBlankContent() + 1);
        }
        else if(!pLogDTO.getMessageContent().matches("^[0-9a-zA-Z :\\.]+$"))
        {
            pRow.setFieldsErrors(true);
        }
        else
        {
            for(String word : pLogDTO.getMessageContent().split(" "))
            {
                if(!pMetricsDTO.getWordOccurrenceMap().containsKey(word))
                {
                    pMetricsDTO.getWordOccurrenceMap().put(word, 0);
                }

                int previousCountValue = pMetricsDTO.getWordOccurrenceMap().get(word);
                pMetricsDTO.getWordOccurrenceMap().put(word, previousCountValue + 1);
            }
        }

        if(!pLine.contains(MessageFormat.format("\"{0}\"", Constant.MESSAGE_STATUS)))
        {
            pRow.setMissingFields(true);
        }
        else if(!pLogDTO.getMessageStatus().matches(MessageFormat.format("^({0}|{1})$", Constant.DELIVERED, Constant.SEEN)))
        {
            pRow.setFieldsErrors(true);
        }

        if(pRow.getFieldsErrors())
        {
            pMetricsDTO.setNRowsFieldsErrors(pMetricsDTO.getNRowsFieldsErrors() + 1);
        }

        if(pRow.getMissingFields())
        {
            pMetricsDTO.setNRowsMissingFields(pMetricsDTO.getNRowsMissingFields() + 1);
        }
    }

    @Override
    public List<ProcessedJSON> getProcessedJSONList()
    {
        return processedJSONRepository.findAll();
    }

    @Override
    public List<Row> getRowList()
    {
        return rowRepository.findAll();
    }

    @Override
    public List<Call> getCallList()
    {
        return callRepository.findAll();
    }

    @Override
    public List<Message> getMessageList()
    {
        return messageRepository.findAll();
    }
}
