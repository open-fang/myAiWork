package com.auth.letter.enums;

/**
 * Organization Level
 * Hierarchy: 机关 > 地区部 > 代表处 > 办事处
 */
public enum OrgLevel {
    ORGANIZATION(1, "机关"),
    REGIONAL_DEPT(2, "地区部"),
    REPRESENTATIVE_OFFICE(3, "代表处"),
    OFFICE(4, "办事处");

    private final int level;
    private final String description;

    OrgLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if current level is higher than or equal to target level
     */
    public boolean isHigherOrEqualTo(OrgLevel target) {
        return this.level <= target.level;
    }
}