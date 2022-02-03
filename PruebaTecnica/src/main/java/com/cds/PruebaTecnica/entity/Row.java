package com.cds.PruebaTecnica.entity;

import com.cds.PruebaTecnica.constant.Constant;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "json_row")
@Data
@Accessors(chain = true)
public class Row implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "json_id")
    private ProcessedJSON processedJSON;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String destination;

    @Column(name = "missing_fields")
    private Boolean missingFields;

    @Column(name = "fields_errors")
    private Boolean fieldsErrors;

    @OneToOne(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
    private Call call;

    @OneToOne(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
    private Message message;
}
