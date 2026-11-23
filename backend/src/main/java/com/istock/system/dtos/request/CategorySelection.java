package com.istock.system.dtos.request;

import jakarta.validation.constraints.Positive;

public record CategorySelection(@Positive Long id) {}
