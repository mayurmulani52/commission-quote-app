package com.bendigobank.commissionquote.dto;

/**
 * NOTE: the challenge brief does not define the allowed riskBand values.
 * LOW / MEDIUM / HIGH was chosen as a self-explanatory default; a real
 * integration would use whatever enumeration the vendor contract defines.
 */
public enum RiskBand {
    LOW,
    MEDIUM,
    HIGH
}
