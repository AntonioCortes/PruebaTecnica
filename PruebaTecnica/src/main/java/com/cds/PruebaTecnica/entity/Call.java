package com.cds.PruebaTecnica.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "call")
@Data
@Accessors(chain = true)
public class Call implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "row_id")
    private Row row;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "status_description")
    private String statusDescription;
}