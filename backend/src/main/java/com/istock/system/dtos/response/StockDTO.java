package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StockDTO(
        @JsonProperty("minimum_level")
        Integer minimumLevel,
        @JsonProperty("current_balance")
        Integer currentBalance,
        @JsonProperty("on_alert")
        Boolean onAlert
) {}
