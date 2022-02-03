package com.cds.PruebaTecnica.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "message")
@Data
@Accessors(chain = true)
public class Message implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "row_id")
    private Row row;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "message_status")
    private String messageStatus;
}
