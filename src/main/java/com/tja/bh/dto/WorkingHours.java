package com.tja.bh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class WorkingHours {
    private Date open;
    private Date close;
}
