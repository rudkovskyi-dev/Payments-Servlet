package ua.rudkovskyi.payments.bean;

public class Balance {
    private Long id;
    private String name;
    private Long amount;
    private Double doubleAmount;
    private boolean isLocked;
    private boolean isRequested;
    private User owner;

    public Balance() {
    }

    public Balance(Long id, String name, Long amount, boolean isLocked, boolean isRequested, User owner) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
        this.isLocked = isLocked;
        this.isRequested = isRequested;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
    }

    public Double getDoubleAmount() {
        return doubleAmount;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public boolean getIsRequested() {
        return isRequested;
    }

    public void setRequested(boolean requested) {
        isRequested = requested;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
