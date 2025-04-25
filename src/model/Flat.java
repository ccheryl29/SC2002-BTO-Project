package model;

import java.io.Serializable;

/**
 * Represents a specific flat type in a project.
 */
public class Flat implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Enum representing the types of flats available
     */
    public enum FlatType {
        TWO_ROOM("2-Room"),
        THREE_ROOM("3-Room");
        
        private final String displayName;
        
        FlatType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * Gets a FlatType from a display name string
         * 
         * @param displayName The display name to match
         * @return The matching FlatType or null if no match
         */
        public static FlatType fromDisplayName(String displayName) {
            for (FlatType type : FlatType.values()) {
                if (type.displayName.equalsIgnoreCase(displayName)) {
                    return type;
                }
            }
            return null;
        }
    }
    
    private FlatType flatType;
    private int totalUnits;
    private int availableUnits;
    private long sellingPrice;
    
    /**
     * Constructor for Flat
     * 
     * @param flatType Type of the flat
     * @param totalUnits Total number of units available
     * @param sellingPrice Selling price of the flat
     */
    public Flat(FlatType flatType, int totalUnits, long sellingPrice) {
        this.flatType = flatType;
        this.totalUnits = totalUnits;
        this.availableUnits = totalUnits; // Initially, all units are available
        this.sellingPrice = sellingPrice;
    }
    
    /**
     * Gets the flat type
     * 
     * @return The flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }
    
    /**
     * Sets the flat type
     * 
     * @param flatType The new flat type
     */
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
    
    /**
     * Gets the total number of units
     * 
     * @return The total number of units
     */
    public int getTotalUnits() {
        return totalUnits;
    }
    
    /**
     * Sets the total number of units
     * 
     * @param totalUnits The new total number of units
     */
    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }
    
    /**
     * Gets the number of available units
     * 
     * @return The number of available units
     */
    public int getAvailableUnits() {
        return availableUnits;
    }
    
    /**
     * Sets the number of available units
     * 
     * @param availableUnits The new number of available units
     */
    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }
    
    /**
     * Gets the selling price of the flat
     * 
     * @return The selling price
     */
    public long getSellingPrice() {
        return sellingPrice;
    }
    
    /**
     * Sets the selling price of the flat
     * 
     * @param sellingPrice The new selling price
     */
    public void setSellingPrice(long sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    
    /**
     * Checks if there are available units
     * 
     * @return true if there are available units, false otherwise
     */
    public boolean isAvailable() {
        return availableUnits > 0;
    }
    
    /**
     * Books a unit, decreasing the number of available units
     * 
     * @return true if booking was successful, false if no units available
     */
    public boolean bookUnit() {
        if (isAvailable()) {
            availableUnits--;
            return true;
        }
        return false;
    }
    
    /**
     * Reduces the number of available units
     * 
     * @return true if reduction was successful, false if no units available
     */
    public boolean reduceAvailableUnits() {
        return bookUnit();
    }
    
    @Override
    public String toString() {
        return "Flat Type: " + flatType.getDisplayName() +
               "\nTotal Units: " + totalUnits +
               "\nAvailable Units: " + availableUnits +
               "\nSelling Price: $" + sellingPrice;
    }
} 