package kz.halyk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Record {
    private Date date;
    private String description;
    private BigDecimal withdrawal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record record)) return false;
        return getDate().equals(record.getDate()) && getDescription().equals(record.getDescription()) && Objects.equals(getWithdrawal(), record.getWithdrawal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getDescription());
    }
}
