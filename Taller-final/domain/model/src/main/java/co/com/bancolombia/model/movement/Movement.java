package co.com.bancolombia.model.movement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class Movement {

    private String movementId;
    private String boxId;
    private LocalDateTime date;
    private MovementType type;
    private BigDecimal amount;
    private String currency;
    private String description;
    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private static final Set<String> ALLOWED_CURRENCIES = Set.of("USD", "PEN", "COP", "PER");
    public static Movement toRecord(Map<String, String> record, String providedBoxId, Set<String> processedIds) {
        String movementId = record.get("movementId");
        String boxIdFile = record.get("boxId");
        String dateStr = record.get("date");
        String type = record.get("type");
        String amountStr = record.get("amount");
        String currency = record.get("currency");
        String description = record.get("description");


        if (boxIdFile == null || boxIdFile.isBlank())
            throw new IllegalArgumentException("Box ID cannot be empty: " + record);

        if (!boxIdFile.equals(providedBoxId))
            throw new IllegalArgumentException("Box ID in record does not match provided boxId");

        if (movementId == null || movementId.isBlank())
            throw new IllegalArgumentException("Movement ID cannot be empty: " + record);

        if (!processedIds.add(movementId))
            throw new IllegalArgumentException("Duplicate movement ID found: " + movementId);

        if (dateStr == null || dateStr.isBlank() || !isValidISO8601(dateStr))
            throw new IllegalArgumentException("Invalid date format. Expected ISO 8601: " + dateStr);

        LocalDateTime date = LocalDateTime.parse(dateStr);

        if (!"INCOME".equalsIgnoreCase(type) && !"EXPENSE".equalsIgnoreCase(type))
            throw new IllegalArgumentException("Invalid type. Expected INCOME or EXPENSE: " + type);

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a valid number: " + amountStr);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero: " + amount);

        if (!ALLOWED_CURRENCIES.contains(currency))
            throw new IllegalArgumentException("Invalid currency. Allowed: " + ALLOWED_CURRENCIES + ". Provided: " + currency);

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description cannot be empty: " + record);

        Movement movement = new Movement();
        movement.setMovementId(movementId);
        movement.setBoxId(providedBoxId);
        movement.setDate(date);
        movement.setType(MovementType.valueOf(type.toUpperCase()));
        movement.setAmount(amount);
        movement.setCurrency(currency);
        movement.setDescription(description);

        return movement;
    }
    private static boolean isValidISO8601(String date) {
        try {
            LocalDateTime.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
