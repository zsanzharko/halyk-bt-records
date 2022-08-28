package kz.halyk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class TrimRecord {
    private Date date;
    private String description;
    private BigDecimal withdrawal;
}