package kz.halyk.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
@Builder
public class OutputRecord {
    private Date date;
    private String type;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal average;

    public OutputRecord(Date date, String type, BigDecimal min, BigDecimal max, BigDecimal average) {
        this.date = date;
        setType(type);
        this.min = min;
        this.max = max;
        this.average = average;
    }

    public void setType(String type) {
        if (type.isEmpty()) {
            this.type = type;
            return;
        }
        this.type = String.format("Description=\"%s\"", type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutputRecord that)) return false;
        return getDate().getTime() == that.getDate().getTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate().getTime());
    }
}
