package com.cds.PruebaTecnica.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "processed_json")
@Data
@Accessors(chain = true)
public class ProcessedJSON implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private String date;

    @Column(name = "process_duration")
    private Long processDuration;

    @OneToMany(mappedBy = "processedJSON", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Row> rowList;
}
