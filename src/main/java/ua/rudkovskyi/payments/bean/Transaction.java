package ua.rudkovskyi.payments.bean;

public class Transaction {
    private Long id;
    private Balance source;
    private Balance destination;
    private Long amount;
    private Double doubleAmount;
    private boolean isSent;

    public Transaction() {
    }

    public Transaction(Long amount) {
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
        this.isSent = false;
    }

    public Transaction(Long id, Balance source, Balance destination, Long amount, boolean isSent) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
        this.isSent = isSent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Balance getSource() {
        return source;
    }

    public void setSource(Balance source) {
        this.source = source;
    }

    public Balance getDestination() {
        return destination;
    }

    public void setDestination(Balance destination) {
        this.destination = destination;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Double getDoubleAmount() {
        return doubleAmount;
    }

    public void setDoubleAmount(Double doubleAmount) {
        this.doubleAmount = doubleAmount;
    }

    public boolean isSent() {
        return isSent;
    }

    public boolean getIsSent(){
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
