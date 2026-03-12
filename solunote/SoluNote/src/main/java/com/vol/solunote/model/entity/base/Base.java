package com.vol.solunote.model.entity.base;


import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@MappedSuperclass
@Getter
@Setter

public abstract class Base extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int seq;
}