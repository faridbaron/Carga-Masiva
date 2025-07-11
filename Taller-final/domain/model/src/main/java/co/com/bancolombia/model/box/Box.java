package co.com.bancolombia.model.box;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Box {

    private String id;                // ID de la caja
    private String name;              // Nombre o número de caja
    private BoxStatus status;         // Estado: OPENED, CLOSED
    private BigDecimal openingAmount; // Monto de apertura
    private BigDecimal closingAmount; // Monto de cierre
    private LocalDateTime openedAt;   // Fecha y hora de apertura
    private LocalDateTime closedAt;   // Fecha y hora de cierre
    private BigDecimal currentBalance;// Saldo actual de la caja

    public Box(){
    }
    // Constructor privado para obligar a usar el builder
    private Box(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.status = builder.status;
        this.openingAmount = builder.openingAmount;
        this.closingAmount = builder.closingAmount;
        this.openedAt = builder.openedAt;
        this.closedAt = builder.closedAt;
        this.currentBalance = builder.currentBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BoxStatus getStatus() {
        return status;
    }

    public void setStatus(BoxStatus status) {
        this.status = status;
    }

    public BigDecimal getOpeningAmount() {
        return openingAmount;
    }

    public void setOpeningAmount(BigDecimal openingAmount) {
        this.openingAmount = openingAmount;
    }

    public BigDecimal getClosingAmount() {
        return closingAmount;
    }

    public void setClosingAmount(BigDecimal closingAmount) {
        this.closingAmount = closingAmount;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    // Builder
    public static class Builder {
        private String id;
        private String name;
        private BoxStatus status;
        private BigDecimal openingAmount;
        private BigDecimal closingAmount;
        private LocalDateTime openedAt;
        private LocalDateTime closedAt;
        private BigDecimal currentBalance;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(BoxStatus status) {
            this.status = status;
            return this;
        }

        public Builder openingAmount(BigDecimal openingAmount) {
            this.openingAmount = openingAmount;
            return this;
        }

        public Builder closingAmount(BigDecimal closingAmount) {
            this.closingAmount = closingAmount;
            return this;
        }

        public Builder openedAt(LocalDateTime openedAt) {
            this.openedAt = openedAt;
            return this;
        }

        public Builder closedAt(LocalDateTime closedAt) {
            this.closedAt = closedAt;
            return this;
        }

        public Builder currentBalance(BigDecimal currentBalance) {
            this.currentBalance = currentBalance;
            return this;
        }

        public Box build() {
            return new Box(this);
        }
    }

    // Métodos de negocio (sin cambios)
    public void open(BigDecimal amount) {
        if (status == BoxStatus.OPENED) {
            throw new IllegalStateException("La caja ya está abierta");
        }
        this.openingAmount = amount;
        this.currentBalance = amount;
        this.status = BoxStatus.OPENED;
        this.openedAt = LocalDateTime.now();
    }

    public void close(BigDecimal closingAmount) {
        if (status != BoxStatus.OPENED) {
            throw new IllegalStateException("La caja no está abierta");
        }
        this.closingAmount = closingAmount;
        this.status = BoxStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }
    public void addAmount(BigDecimal amount) {
        if (status != BoxStatus.OPENED) {
            throw new IllegalStateException("La caja debe estar abierta para registrar movimientos");
        }
        this.currentBalance = this.currentBalance.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        if (status != BoxStatus.OPENED) {
            throw new IllegalStateException("La caja debe estar abierta para registrar movimientos");
        }
        if (this.currentBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        this.currentBalance = this.currentBalance.subtract(amount);
    }
    public void reopen() {
        if (status != BoxStatus.CLOSED) {
            throw new IllegalStateException("La caja no se encuentra cerrada");
        }
        this.status = BoxStatus.OPENED;
        this.openedAt = LocalDateTime.now();
    }

    public void updateName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la caja no puede ser nulo o vacío");
        }
        if (newName.equals(this.name)) {
            throw new IllegalArgumentException("El nuevo nombre debe ser diferente al actual");
        }
        this.name = newName;
    }

    public void delete() {
        if (status == BoxStatus.DELETED) {
            throw new IllegalStateException("La caja ya está eliminada");
        }
        this.status = BoxStatus.DELETED;
    }

}
