package kz.halyk.utils.enums;

public enum BTColumns {
    DATE("Date", 0),
    DESCRIPTION("Description", 1),
    DEPOSIT("Deposit", 2),
    WITHDRAWALS("Withdrawls", 3),
    BALANCE("Balance",4);

    private final String name;
    private final int index;

    BTColumns(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
